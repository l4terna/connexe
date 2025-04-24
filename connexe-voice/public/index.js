import io from 'socket.io-client';
import {Device} from 'mediasoup-client';

const socket = io("/mediasoup");

let device;
let rtpCapabilities;
let producerTransport;
let consumingTransports = [];
let consumerTransports = [];
let videoProducer;
let audioProducer;
let roomName = window.location.pathname.split('/')[2] + '12313123';

let params = {
    encoding: [
        {
            'rid': 'r0',
            maxBitrate: 100000,
            scalabilityMode: 'S1T3'
        },
        {
            'rid': 'r1',
            maxBitrate: 300000,
            scalabilityMode: 'S1T3'
        },
        {
            'rid': 'r2',
            maxBitrate: 900000,
            scalabilityMode: 'S1T3'
        },
    ],
    codecOptions: {
        videoGoogleStartBitrate: 1000
    }
};

let audioParams = {};
let videoParams = {...params};

const streamSuccess = async (stream) => {
    localVideo.srcObject = stream;

    audioParams = { track: stream.getAudioTracks()[0], ...audioParams };
    videoParams = { track: stream.getVideoTracks()[0], ...videoParams };

    joinRoom();
}

const getLocalStream = () => {
    navigator.mediaDevices.getUserMedia({
        audio: true,
        video: {
            width: {
                min: 640,
                max: 1920
            },
            height: {
                min: 400,
                max: 1080
            }
        }
    })
        .then(streamSuccess)
        .catch(error => {
            console.error("Ошибка доступа к камере:", error.message);
        });
};

socket.on('connection-success', ({socketId}) => {
    console.log(socketId);
});

const joinRoom = () => {
    socket.emit('joinRoom', { roomName }, async (data) => {
        rtpCapabilities = data.rtpCapabilities;

        await createDevice();
    });
};

socket.on('new-producer', ({producerId}) => signalNewConsumerTransport(producerId));

const signalNewConsumerTransport = async (remoteProducerId) => {
    if (consumingTransports.includes(remoteProducerId)) return;

    consumingTransports.push(remoteProducerId);

    await socket.emit('createWebRtcTransport', {consumer: true}, ({params}) => {
        if (params.error) {
            return;
        }

        let consumerTransport;

        try {
            consumerTransport = device.createRecvTransport(params);
        } catch (error) {
            console.error(error);
            return;
        }

        consumerTransport.on('connect', async ({dtlsParameters}, callback, errback) => {
            try {
                await socket.emit('transport-recv-connect', {
                    dtlsParameters,
                    serverConsumerTransportId: params.id,
                });

                callback();
            } catch (error) {
                errback(error);
            }
        });

        connectRecvTransport(consumerTransport, remoteProducerId, params.id);
    });
};

const createDevice = async () => {
    try {
        device = new Device();

        await device.load({routerRtpCapabilities: rtpCapabilities});

        console.log('RTP Capabilities', device.rtpCapabilities)

        await createSendTransport();
    } catch (err) {
        console.error(err.message);
        if (err.name === 'UnsupportedError') {
            console.error('Browser not supported');
        }
    }
};

const createSendTransport = async () => {
    socket.emit('createWebRtcTransport', {consumer: false}, async ({params}) => {
        if (params.error) {
            console.log(params.error);
            return;
        }

        producerTransport = await device.createSendTransport(params);

        producerTransport.on('connect', async ({dtlsParameters}, callback, errback) => {
            try {
                await socket.emit('transport-connect', {
                    dtlsParameters: dtlsParameters
                });

                callback();
            }  catch (err) {
                errback(err);
            }
        });

        producerTransport.on('produce', async (params, callback, errback) => {
            console.log('produce', params);
            try {
                await socket.emit('transport-produce', {
                    kind: params.kind,
                    rtpParameters: params.rtpParameters,
                    appData: params.appData,
                }, ({producerId, producersExists}) => {
                    callback({producerId});

                    if (producersExists) getProducers();
                });
            } catch (err) {
                errback(err);
            }
        });

        await connectSendTransport();
    });
};


const getProducers = () => {
    socket.emit('getProducers', roomName, producerIds => {
        producerIds.forEach(producerId => signalNewConsumerTransport(producerId));
    });
};

const connectSendTransport = async () => {
    params = {
        codecs: device.rtpCapabilities.codecs,
        ...params
    };

    audioProducer = await producerTransport.produce(audioParams);
    videoProducer = await producerTransport.produce(videoParams);

    audioProducer.on('trackended', () => {
        console.log('trackended');
        // do something
    });

    audioProducer.on('transportclose', () => {
        console.log('transport closed');
        // do something
    });

    videoProducer.on('trackended', () => {
        console.log('trackended');
        // do something
    });

    videoProducer.on('transportclose', () => {
        console.log('transport closed');
        // do something
    });
};

const connectRecvTransport = async (consumerTransport, remoteProducerId, serverConsumerTransportId) => {
    await socket.emit('consume', {
        rtpCapabilities: device.rtpCapabilities,
        remoteProducerId,
        serverConsumerTransportId
    }, async ({ params }) => {
        if (params.error) {
            console.log('Cannot Consume', params.error)
            return
        }

        const consumer = await consumerTransport.consume({
            id: params.id,
            producerId: params.producerId,
            kind: params.kind,
            rtpParameters: params.rtpParameters
        });

        consumerTransports = [
            ...consumerTransports,
            {
                consumerTransport,
                serverConsumerTransportId: params.id,
                producerId: remoteProducerId,
                consumer
            }
        ];

        const newElem = document.createElement('div');
        newElem.setAttribute('id', `td-${remoteProducerId}`);

        let mediaElem;
        if (params.kind == 'audio') {
            //append to the audio container
            newElem.innerHTML = '<audio id="' + remoteProducerId + '" autoplay></audio>';
            mediaElem = newElem.querySelector('audio');
        } else {
            //append to the video container
            newElem.setAttribute('class', 'remoteVideo');
            newElem.innerHTML = '<video id="' + remoteProducerId + '" autoplay class="video" ></video>';
            mediaElem = newElem.querySelector('video');
        }

        videoContainer.appendChild(newElem);

        const { track } = consumer;
        mediaElem.srcObject = new MediaStream([track]);

        socket.emit('consumer-resume', { serverConsumerId: params.serverConsumerId })
    })
}

socket.on('producer-closed', ({ remoteProducerId }) => {
    const producerToClose = consumerTransports.find(transportData => transportData.producerId === remoteProducerId);

    if (producerToClose) {
        producerToClose.consumerTransport.close();
        producerToClose.consumer.close();

        consumerTransports = consumerTransports.filter(transportData => transportData.producerId !== remoteProducerId);

        videoContainer.removeChild(document.getElementById(`td-${remoteProducerId}`));
    }
});

const cleanup = () => {
    device = null;
    rtpCapabilities = null;
    producerTransport = null;
    console.log(consumingTransports)
    consumingTransports = [];
    console.log(consumingTransports)
    consumerTransports = [];
    videoProducer = null;
    audioProducer = null;

    videoContainer.innerHTML = "";
};

startBtn.addEventListener('click', () => {
    roomName = roomname.value;

    cleanup();
    console.log(consumingTransports)
    getLocalStream();
});