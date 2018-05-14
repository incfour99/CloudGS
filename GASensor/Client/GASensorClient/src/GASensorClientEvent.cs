using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GASensorClient
{
    partial class GASensorClient
    {
        private void OnShot(ShotData shotData)
        {
            // 서버 정책 요청
            var req = new ReqShot
            {
                ballSpeed = shotData.ballSpeed,
                ballIncidence = shotData.ballIncidence,
                ballDir = shotData.ballDir,
                backSpin = shotData.backSpin,
                sideSpin = shotData.sideSpin
            };

            if (!netMng.BeginSendJSON(req))
            {
                Logger.WriteLine("[GASensorClient] Failed to send ReqShot.");
                return;
            }            
        }
    }
}
