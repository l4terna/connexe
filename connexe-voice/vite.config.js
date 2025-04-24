import { defineConfig } from 'vite';
import fs from 'fs';
import path from 'path';

export default defineConfig({
    server: {
        https: {
            key: fs.readFileSync(path.resolve(__dirname, './server/ssl/key.pem')),
            cert: fs.readFileSync(path.resolve(__dirname, './server/ssl/cert.pem')),
        },
        host: '0.0.0.0',
        proxy: {
            '/socket.io': {
                target: 'http://localhost:3000/socket.io',
                ws: true
            }
        }
    }
});