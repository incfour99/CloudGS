using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace GASensorClient
{
    partial class GASensorClient
    {
        public void OnAckServerPolicy(string message)
        {
            AckServerPolicy ack = NetUtils.DeserializeJSON<AckServerPolicy>(message);

            if (ack.result != 1)
            {
                Logger.WriteLine("[RecvDataHandler] OnAckServerPolicy result fail. result code: " + ack.result);
                return;
            }

            // GA Server 정보 요청
            var req = new ReqGAServerInfo
            {
                dummy = 0
            };

            if (!netMng.BeginSendJSON(req))
            {
                Logger.WriteLine("[GASensorClient] Failed to send ReqGAServerInfo.");
                return;
            }
        }

        public void OnAckGAServerInfo(string message)
        {
            AckGAServerInfo ack = NetUtils.DeserializeJSON<AckGAServerInfo>(message);

            if (ack.result != 1)
            {
                Logger.WriteLine("[RecvDataHandler] OnAckGAServerInfo result fail. result code: " + ack.result);
                return;
            }

            gaLauncher = new GALauncher();
            gaLauncher.Start(ack.ip, ack.port);
        }

        public void OnAckShot(string message)
        {
            AckShot ack = NetUtils.DeserializeJSON<AckShot>(message);

            if (ack.result != 1)
            {
                Logger.WriteLine("[RecvDataHandler] OnAckShot result fail. result code: " + ack.result);
                return;
            }
        }
    }
}
