var winston = require('winston');
var net = require("net");
var colors = require("colors");
var requestHandler = require("./requestHandler");

function start()
{
  var server = net.createServer();

  server.on("connection", function(socket) {

    var remoteAddress = socket.remoteAddress + ":" + socket.remotePort;
    winston.info("new client connection is made " + remoteAddress);

    socket.on("data", function(d) {
      winston.info("Data from " + remoteAddress + " " + d);
	  
	  if(d == null || d == undefined) {
		  winston.error("Data is invalid.");
		  return;
	  }

      try {
          var json = JSON.parse(d);
		  winston.info('parsed json : ' + json);
      } catch (e) {
          winston.error("received data is not JSON");
          return;
      }
	  
	  if(json.header == undefined) {
		  winston.error("header is undefined. " + d);
		  return;
	  }

      if (typeof requestHandler.handle[json.header] === 'function') {
        requestHandler.handle[json.header](socket, json);
      }
      else {
        winston.error("no request handler found for " + d);
      }
    });

    socket.once("close", function() {
      winston.info("Connection from %s closed" + remoteAddress);
    });

    socket.on("error", function(err) {
      winston.error("Connection %s error: %s" + remoteAddress, err.message);
    });
  });

  server.listen(9000, function(){
	addressObj = server.address();  
    winston.info("server listening to IP : " + addressObj.address + " PORT : " + addressObj.port);
  });
}

exports.start = start;
