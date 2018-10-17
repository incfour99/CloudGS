var winston = require('winston');

function AckResisterServer(socket, data) {
    winston.info('AckResisterServer called ');
}

function ReqShot(socket, data) {
  winston.info('ReqShot called ');

  var c_Addon = require('./module/C_Addon');
  var c_AddonHandler = new c_Addon();

  winston.info("c_AddonHandler : " + c_AddonHandler);
  winston.info("c_AddonHandler : " + c_AddonHandler.sendMessage);

  c_AddonHandler.sendMessage(
    data.ballSpeed,
    data.ballIncidence,
    data.ballDir,
    data.backSpin,
    data.sideSpin
  );

  winston.info('ReqShot called sendMessage end');

  var ack = {
    header : "AckShot",
    result : 1
  };

  var jsonString = JSON.stringify(ack) + '\r\n';
  socket.write(jsonString);

  winston.info('AckShot sended');
}

var handle = {}; // javascript object has key:value pair.
handle['AckResisterServer'] = AckResisterServer;
handle['ReqShot'] = ReqShot;

exports.handle = handle;
