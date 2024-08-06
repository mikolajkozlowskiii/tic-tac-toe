const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

app.use(express.static('public'));

const targetAddress = process.env.PROXY_TARGET || 'http://backend:8080';

app.use('/game', createProxyMiddleware({ 
  target: targetAddress, 
  ws: true, 
  changeOrigin: true 
}));

app.listen(8081, () => {
  console.log('Server is running on port 8081');
});
