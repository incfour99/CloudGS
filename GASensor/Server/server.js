var net = require("net");
var colors = require("colors");
var requestHandler = require("./requestHandler");

function start()
{
  var server = net.createServer();

  server.on("connection", function(socket) {

    var remoteAddress = socket.remoteAddress + ":" + socket.remotePort;
    console.log("new client connection is made %s".green, remoteAddress);

    socket.on("data", function(d) {
      console.log("Data from %s: %s".cyan, remoteAddress, d);

      try {
          var json = JSON.parse(d);
      } catch (e) {
          console.log("received data is not JSON");
          return;
      }

      if (typeof requestHandler.handle[json.header] === 'function') {
        requestHandler.handle[json.header](socket, json);
      }
      else {
        console.log("no request handler found for " + d);
      }
    });

    socket.once("close", function() {
      console.log("Connection from %s closed".yellow, remoteAddress);
    });

    socket.on("error", function(err) {
      console.log("Connection %s error: %s".red, remoteAddress, err.message);
    });
  });

  server.listen(9000, function(){
    console.log("server listening to %j", server.address());
  });
}

exports.start = start;
