var addon = require('./build/Release/addon');
var obj = new addon.MyObject(10);

function C_AddonHandler(){
}

C_AddonHandler.prototype.sendMessage = function(arg0, arg1, arg2, arg3, arg4){
   //console.log( 'aaaa' );

   obj.CMD_SendWinMSG(arg0, arg1, arg2, arg3, arg4);
}

module.exports = C_AddonHandler;
