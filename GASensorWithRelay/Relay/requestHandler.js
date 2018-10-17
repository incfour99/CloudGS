var winston = require('winston');

var clientSocket, serverSocket;

function ReqResisterClient(socket, data) {
  winston.info('ReqResisterClient called ');

  clientSocket = socket;

  var ack = {
    header : "AckResisterClient",
    result : 1
  };

  var jsonString = JSON.stringify(ack) + '\r\n';
  socket.write(jsonString);

  winston.info('AckResisterClient sended');
}

function ReqResisterServer(socket, data) {
  winston.info('ReqResisterServer called ');

  serverSocket = socket;

  var ack = {
    header : "AckResisterServer",
    result : 1
  };

  var jsonString = JSON.stringify(ack) + '\r\n';
  socket.write(jsonString);

  winston.info('AckResisterServer sended');
}

function ReqShot(socket, data) {
  winston.info('ReqShot called ');

  if(socket != clientSocket) {
    winston.info('ReqShot clientSocket invalid : ' + clientSocket);
    return;
  }

  var req = {
    header : "ReqShot",
    ballSpeed : data.ballSpeed,
    ballIncidence : data.ballIncidence,
    ballDir : data.ballDir,
    backSpin : data.backSpin,
    sideSpin : data.sideSpin,    
    result : 1
  };

  var jsonString = JSON.stringify(req) + '\r\n';
  serverSocket.write(jsonString);

  winston.info('ReqShot relayed to cloud');
}

function AckShot(socket, data) {
  winston.info('AckShot called ');
}

var handle = {}; // javascript object has key:value pair.
handle['ReqResisterClient'] = ReqResisterClient;
handle['ReqResisterServer'] = ReqResisterServer;
handle['ReqShot'] = ReqShot;
handle['AckShot'] = AckShot;

exports.handle = handle;
