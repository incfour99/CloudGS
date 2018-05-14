var http = require('http');
var c_Addon = require('./module/C_Addon');

var c_AddonHandler = new c_Addon();

var server = http.createServer(function(request, response){
  response.writeHead(200, {'Content-Type' : 'text/html'});
  response.end('Node.js Simple Server!!!');
});

server.listen(8080, function(){
    console.log('Server is running');
    c_AddonHandler.sendMessage();
    //addonHandler.sendMsg();
});
