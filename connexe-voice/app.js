import express from 'express';
import https from 'httpolyglot';
import fs from 'fs';
import {Server} from "socket.io";
import mediasoup from 'mediasoup';
import {Eureka} from 'eureka-js-client';
import os from 'os';

const app = express();

const options = {
    key: fs.readFileSync('./server/ssl/key.pem', 'utf-8'),
    cert: fs.readFileSync('./server/ssl/cert.pem', 'utf-8'),
};

const httpServer = https.createServer(options, app);

httpServer.listen(0, () => {
    console.log('Started on port 3000');

    registerWithEureka(httpServer.address().port);
});

const io = new Server(httpServer);
const connection = io.of('/mediasoup');

let worker;
let peers = {};  // { socketId1: { roomName1, socket, transports = [id1, id2,] }, producers = [id1, id2,] }, consumers = [id1, id2,] }, ...}
let rooms = {}; // { roomName1: { Router, peers: [ socketId1, socketId2, ... ] }, ...}
let transports = []; // [ { socketId1, roomName1, consumer, }, ... ]
let producers = []; // [ { socketId1, roomName1, isConsumer, }, ... ]
let consumers = []; // [ { socketId1, roomName1, consumer, }, ... ]

const createWorker = async () => {
    worker = await mediasoup.createWorker({
        rtcMinPort: 2000,
        rtcMaxPort: 2020
    });

    worker.on('died', error => {
        console.error('worker has died');
        process.exit();
    });

    return worker;
};

createWorker();

const mediaCodecs = [
    {
        kind: 'audio',
        mimeType: 'audio/opus',
        payloadType: 100,
        clockRate: 48000,
        channels: 2
    },
    {
        kind: 'video',
        mimeType: 'video/VP8',
        clockRate: 90000,
        payloadType: 101,
        parameters: {
            'x-google-start-bitrate': 1000,
        },
    },
];

connection.on('connection', async (socket) => {
    socket.emit('connection-success', {
        socketId: socket.id,
    });

    socket.on('disconnect', () => {
        cleanup(socket.id);

        if (peers[socket.id]) {
            const { roomName } = peers[socket.id]
            delete peers[socket.id];

            rooms[roomName] = {
                router: rooms[roomName].router,
                peers: rooms[roomName].peers.filter(socketId => socketId !== socket.id),
            };
        }
    });

    socket.on('joinRoom', async ({roomName}, callback) => {
        cleanup(socket.id);
        const router = await createRoom(roomName, socket.id);

        peers[socket.id] = {
            socket,
            roomName,
            transports: [],
            producers: [],
            consumers: [],
        };

        const rtpCapabilities = router.rtpCapabilities;

        callback({rtpCapabilities});
    });

        socket.on('createWebRtcTransport', async ({consumer}, callback) => {
        const roomName = peers[socket.id].roomName;
        const router = rooms[roomName].router;

        createWebRtcTransport(router).then(
            transport => {
                callback({
                    params: {
                        id: transport.id,
                        iceCandidates: transport.iceCandidates,
                        iceParameters: transport.iceParameters,
                        dtlsParameters: transport.dtlsParameters
                    }
                });

                addTransport(transport, roomName, consumer);
            }
        );
    });

    socket.on('transport-connect', async ({dtlsParameters}) => {
        await getRtcTransport(socket.id).connect({dtlsParameters});
    });

    socket.on('transport-produce', async ({kind, rtpParameters, appData}, callback) => {

        const producer = await getRtcTransport(socket.id).produce({
            kind,
            rtpParameters
        });

        const {roomName} = peers[socket.id];

        informConsumers(roomName, socket.id, producer.id);

        addProducer(producer, roomName);

        producer.on('transportclose', () => {
            producer.close();
        });

        callback({producerId: producer.id, producersExists: producers.length > 1});
    });

    socket.on('transport-recv-connect', async ({ dtlsParameters, serverConsumerTransportId }) => {
        try {
            // Changed transportData.consumer to transportData.isConsumer to match addTransport()
            const transportData = transports.find(transportData => (
                transportData.isConsumer && transportData.transport.id === serverConsumerTransportId
            ));

            if (!transportData) {
                console.log('Consumer transport not found:', serverConsumerTransportId);
                return;
            }

            const consumerTransport = transportData.transport;

            // Add a flag to track whether the transport has been connected
            if (transportData.connected) {
                console.log('Transport already connected:', serverConsumerTransportId);
                return;
            }

            await consumerTransport.connect({ dtlsParameters });

            // Mark the transport as connected
            transportData.connected = true;
        } catch (error) {
            console.error('Error connecting transport:', error);
        }
    })

    socket.on('getProducers', (roomName, callback) => {
        const roomPeers = rooms[roomName]?.peers;
        if (roomPeers) {
            callback(producers.filter(producerData => roomPeers.includes(producerData.socketId) && producerData.socketId !== socket.id)
                .map(producerData => producerData.producer.id));
        } else {
            callback([]);
        }
    });

    socket.on('get-producer-stats', async (producerId, callback) => {
        const producerData = producers.find(p => p.producer.id === producerId);
        if (producerData) {
            const stats = await producerData.producer.getStats();
            callback({ producerId, stats });
        }
    });

    socket.on('consume', async ({ rtpCapabilities, remoteProducerId, serverConsumerTransportId }, callback) => {
        try {
            const { roomName } = peers[socket.id];
            const router = rooms[roomName].router;

            let consumerTransport = transports.find(transportData => (
                transportData.isConsumer && transportData.transport.id === serverConsumerTransportId
            )).transport;

            if (router.canConsume({
                producerId: remoteProducerId,
                rtpCapabilities
            })) {
                const consumer = await consumerTransport.consume({
                    producerId: remoteProducerId,
                    rtpCapabilities,
                    paused: true,
                });

                consumer.on('transportclose', () => {
                    console.log('transport close from consumer')
                });

                consumer.on('producerclose', () => {
                    console.log('producer of consumer closed');
                    socket.emit('producer-closed', { remoteProducerId });

                    consumerTransport.close();
                    transports = transports.filter(transportData => transportData.transport.id !== consumerTransport.id)
                    consumer.close()
                    consumers = consumers.filter(consumerData => consumerData.consumer.id !== consumer.id)
                });

                addConsumer(consumer, roomName);

                const params = {
                    id: consumer.id,
                    producerId: remoteProducerId,
                    kind: consumer.kind,
                    rtpParameters: consumer.rtpParameters,
                    serverConsumerId: consumer.id,
                };

                // send the parameters to the client
                callback({ params });
            }
        } catch (error) {
            console.log(error.message)
            callback({
                params: {
                    error: error
                }
            });
        };
    })

    socket.on('consumer-resume', async ({ serverConsumerId }) => {
        const consumerData = consumers.find(data => data.consumer.id === serverConsumerId);

        if (!consumerData) {
            console.log(`Consumer с ID ${serverConsumerId} не найден`);
            return;
        }

        try {
            await consumerData.consumer.resume();
        } catch (error) {
            console.error('Ошибка при возобновлении consumer:', error);
        }
    })

    const informConsumers = (roomName, socketId, producerId) => {
        const room = rooms[roomName];
        if (!room) return;

        room.peers.forEach(peerSocketId => {
            if (peerSocketId !== socketId) {
                const peerSocket = peers[peerSocketId].socket;
                peerSocket.emit('new-producer', { producerId });
            }
        });
    };

    const addTransport = (transport, roomName, isConsumer) => {
        transports = [
            ...transports,
            { socketId: socket.id, transport, roomName, isConsumer }
        ];

        peers[socket.id] = {
            ...peers[socket.id],
            transports: [
                ...peers[socket.id].transports,
                transport.id
            ]
        };
    };

    const addProducer = (producer, roomName) => {
        producers = [
            ...producers,
            { socketId: socket.id, roomName, producer, }
        ];

        peers[socket.id] = {
            ...peers[socket.id],
            producers: [
                ...peers[socket.id].producers,
                producer.id
            ]
        };
    };

    const addConsumer = (consumer, roomName) => {
        consumers = [
            ...consumers,
            { socketId: socket.id, roomName, consumer, }
        ];

        peers[socket.id] = {
            ...peers[socket.id],
            consumers: [
                ...peers[socket.id].consumers,
                consumer.id
            ]
        };
    };

    const getRtcTransport = (socketId) => {
        const producerTransport = transports.find(transport => transport.socketId === socketId && !transport.isConsumer);
        return producerTransport?.transport;
    };
});

const createRoom = async (roomName, socketId) => {
    let router1;
    let peers = [];

    if (rooms[roomName]) {
        router1 = rooms[roomName].router;
        peers = rooms[roomName].peers || [];
    } else {
        router1 = await worker.createRouter({mediaCodecs});
    }

    rooms[roomName] = {
        router: router1,
        peers: [...peers, socketId]
    };

    return router1;
};

const createWebRtcTransport = async (router) => {
    return new Promise(async (resolve, reject) => {
        try {
            const webRtcTransportOptions = {
                listenIps: [
                    {
                        'ip': '0.0.0.0',
                        'announcedIp': '192.168.0.66'
                    }
                ],
                enableUdp: true,
                enableTcp: true,
                preferUdp: true,
            };

            let transport = await router.createWebRtcTransport(webRtcTransportOptions);

            transport.on('dtlsstatechange', dtlsState => {
                if (dtlsState === 'closed') {
                    transport.close();
                }
            });

            transport.observer.on('close', () => {
                transports = transports.filter(transportData => transportData.transport.id !== transport.id);
            });

            resolve(transport);
        }  catch (err) {
            reject(err);
        }
    });
};

const cleanup = (socketId) => {
    consumers = removeItems(consumers, socketId, 'consumer');
    producers = removeItems(producers, socketId, 'producer');
    transports = removeItems(transports, socketId, 'transport');

    const roomName = peers[socketId]?.roomName;

    if (roomName) {
        rooms[roomName] = {
            router: rooms[roomName].router,
            peers: rooms[roomName].peers.filter(peerSocketId => peerSocketId !== socketId),
        };
    }
}

const removeItems = (items, socketId, type) => {
    items.forEach(item => {
        if (item.socketId === socketId && item[type]) {
            item[type].close();
        }
    });

    return items.filter(item => item.socketId !== socketId);
};


function findIpAddr() {
    const networkInterfaces = os.networkInterfaces();

    for (const ifname of Object.keys(networkInterfaces)) {
        for (const iface of networkInterfaces[ifname]) {
            if (!iface.internal && iface.family === 'IPv4') {
                return iface.address;
            }
        }
    }
    return '127.0.0.1';
}

function registerWithEureka(port) {
    const ipAddr = findIpAddr();

    console.log(`Registering with Eureka using IP: ${ipAddr} and Port: ${port}`);

    const client = new Eureka({
        instance: {
            app: 'connexe-voice',
            hostName: `${ipAddr}:connexe-voice:${port}`,
            ipAddr: ipAddr,
            port: {
                '$': port,
                '@enabled': true,
            },
            vipAddress: 'connexe-voice',
            dataCenterInfo: {
                '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
                name: 'MyOwn',
            },
            preferIpAddress: true,
            statusPageUrl: `http://${ipAddr}:${port}/info`,
            healthCheckUrl: `http://${ipAddr}:${port}/health`,
            homePageUrl: `http://${ipAddr}:${port}/`
        },
        eureka: {
            host: ipAddr,
            port: 8761,
            servicePath: '/eureka/apps/',
            maxRetries: 10,
            requestRetryDelay: 2000,
            registryFetchInterval: 30000,
            registerWithEureka: true
        }
    });

    client.on('started', () => {
        console.log('Eureka client started');
    });

    client.on('registered', () => {
        console.log('Service registered with Eureka');
    });

    client.on('error', (error) => {
        console.log('Eureka client error:', error);
    });

    client.start();

    process.on('SIGINT', () => {
        client.stop();
        process.exit();
    });
}