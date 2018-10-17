var logger = require('./logger');
var winston = require('winston');
var msgHandler = require("./msgHandler");

winston.info("CLOUD GS SERVER STARTED!!");

var net = require('net');
function getConnection(connName){
  var client = net.connect({port: 8900, host:'localhost'}, function() {
    console.log(connName + ' Connected: ');
    console.log('   local = %s:%s', this.localAddress, this.localPort);
    console.log('   remote = %s:%s', this.remoteAddress, this.remotePort);
    
    //this.setEncoding('utf8');

    this.on('data', function(data) {
      console.log(connName + " From Server: " + data.toString());   
    
      if(data == null || data == undefined) {
        winston.error("Data is invalid.");
        return;
      }

      try {
          var json = JSON.parse(data);
		  winston.info('parsed json : ' + json);
      } catch (e) {
          winston.error("received data is not JSON");
          return;
      }
	  
      if(json.header == undefined) {
        winston.error("header is undefined. " + data);
        return;
      }

      if (typeof msgHandler.handle[json.header] === 'function') {
        msgHandler.handle[json.header](this, json);
      }
      else {
        winston.error("no msg handler found for " + data);
      }

    });

    this.on('end', function() {
      console.log(connName + ' Client disconnected');
    });

    this.on('error', function(err) {
      console.log('Socket Error: ', JSON.stringify(err));
    });

    this.on('timeout', function() {
      console.log('Socket Timed Out');
    });

    this.on('close', function() {
      console.log('Socket Closed');
    });
  });
  return client;
}

function writeData(socket, data){
  var success = !socket.write(data);
  if (!success){
    (function(socket, data){
      socket.once('drain', function(){
        writeData(socket, data);
      });
    })(socket, data);
  }
}

var Dwarves = getConnection("server");

var req = { header : "ReqResisterServer", result : 1 };
var jsonString = JSON.stringify(req) + '\r\n';

writeData(Dwarves, jsonString);