function ReqSeverPolicy(socket, data) {
    console.log('ReqSeverPolicy called ');

    var ack = {
      header : "AckServerPolicy",
      expireTime : 10.0,
      result : 1
    };

    var jsonString = JSON.stringify(ack);
    socket.write(jsonString);
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
}

function ReqShot(socket, data) {
  var c_Addon = require('./module/C_Addon');
  var c_AddonHandler = new c_Addon();

  c_AddonHandler.sendMessage();

  console.log('ReqShot called ');

  var ack = {
    header : "AckShot",
    result : 1
  };

  var jsonString = JSON.stringify(ack);
  socket.write(jsonString);
}

var handle = {}; // javascript object has key:value pair.
handle['ReqSeverPolicy'] = ReqSeverPolicy;
handle['ReqGAServerInfo'] = ReqGAServerInfo;
handle['ReqShot'] = ReqShot;

exports.handle = handle;
