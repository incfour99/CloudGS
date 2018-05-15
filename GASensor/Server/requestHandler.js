function ReqSeverPolicy(socket, data) {
    console.log('ReqSeverPolicy called ');

    var ack = {
      header : "AckServerPolicy",
      expireTime : 10.0,
      result : 1
    };

    var jsonString = JSON.stringify(ack);
    socket.write(jsonString);

    console.log('AckServerPolicy sended');
}

function ReqGAServerInfo(socket, data) {
  console.log('ReqGAServerInfo called ');

  var ack = {
    header : "AckGAServerInfo",
    ip : "",
    port : "",
    result : 1
  };

  var jsonString = JSON.stringify(ack);
  socket.write(jsonString);

  console.log('AckGAServerInfo sended');
}

function ReqShot(socket, data) {
  console.log('ReqShot called ');

  var c_Addon = require('./module/C_Addon');
  var c_AddonHandler = new c_Addon();

  console.log("c_AddonHandler : " + c_AddonHandler);
  console.log("c_AddonHandler : " + c_AddonHandler.sendMessage);

  c_AddonHandler.sendMessage(
    data.ballSpeed,
    data.ballIncidence,
    data.ballDir,
    data.backSpin,
    data.sideSpin
  );

  console.log('ReqShot called sendMessage end');

  var ack = {
    header : "AckShot",
    result : 1
  };

  var jsonString = JSON.stringify(ack);
  socket.write(jsonString);

  console.log('AckShot sended');
}

var handle = {}; // javascript object has key:value pair.
handle['ReqSeverPolicy'] = ReqSeverPolicy;
handle['ReqGAServerInfo'] = ReqGAServerInfo;
handle['ReqShot'] = ReqShot;

exports.handle = handle;
