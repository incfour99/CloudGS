var logger = require('./logger');
var winston = require('winston');
var server = require("./server");

server.start();
winston.info("CLOUD GS SERVER STARTED!!");

