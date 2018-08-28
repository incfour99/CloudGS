const fs = require('fs');
const path = require('path');
const winston = require('winston');
const moment = require('moment');
const logDir = 'log';

if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir);
}

function getTimestamp() {
    return moment().format('YYYY_MM_DD_HH_mm_ss');                            
};

const fn = path.join(__dirname, logDir, getTimestamp());
winston.configure({
  transports: [
    new winston.transports.File({ filename: fn + ".log" }),
	new winston.transports.Console(),
  ],
  exceptionHandlers: [
    new winston.transports.File({ filename: fn + "except.log" })
  ]
});