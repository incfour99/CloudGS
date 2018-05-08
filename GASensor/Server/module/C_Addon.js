var addon = require('./build/Release/addon');
var obj = new addon.MyObject(10);

function C_AddonHandler(){
}

C_AddonHandler.prototype.sendMessage = function(){
   //console.log( 'aaaa' );

   obj.CMD_SendWinMSG();
}

module.exports = C_AddonHandler;
