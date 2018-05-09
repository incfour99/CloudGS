using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace GASensorClient
{
    partial class GASensorClient
    {
        // INetManagerListener OnConnectServer
        // 서버 접속에 대한 콜백함수
        public override void OnConnectServer(bool isError, string message)
        {
            if (isError)
            {
                Logger.WriteLine("[GASensorClient] OnConnectServer Error : " + message);
                return;
            }

            // 서버 정책 요청
            var req = new ReqSeverPolicy
            {
                dummy = 0
            };

            if (!netMng.BeginSendJSON(req))
            {
                Logger.WriteLine("[GASensorClient] Failed to send ReqSeverPolicy.");
                return;
            }
        }

        // INetManagerListener OnReceiveData
        // 서버에서 보내온 패킷 데이터
        public override void OnReceiveData(bool isError, string message)
        {
            if (isError)
            {
                Logger.WriteLine("[GASensorClient] OnReceiveData Error : " + message);
                return;
            }

            // parsing json packet   
            try
            {
                string header = NetUtils.GetHeader(message);
                MethodInfo theMethod = GetType().GetMethod("On" + header);
                theMethod.Invoke(this, new object[] { message });
            }
            catch (Exception e)
            {
                Logger.WriteLine("[GASensorClient] Error OnReceiveData. Msg: " + e.Message);
                return;
            }
        }

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
