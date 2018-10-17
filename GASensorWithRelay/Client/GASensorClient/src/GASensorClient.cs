using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using GASensorClient;
using System.Web.Script.Serialization;
using System.Reflection;

namespace GASensorClient
{
    partial class GASensorClient : INetManager
    {
        private NetManager netMng;        
        private GALauncher gaLauncher;
        private MsgWnd msgWnd;

        public void Start()
        {
            // 센싱 이벤트 받을 메시지 윈도우 생성
            msgWnd = new MsgWnd(OnShotFromWnd);

            // 네트워크 매니저 생성
            netMng = new NetManager(this);

            //  서버 접속 시도
            if (!netMng.BeginConnect())
            {
                Logger.WriteLine("[GASensorClient] Failed to connect NetManager!");
                return;
            }
        }

        private void OnShotFromWnd(ShotData shotData)
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
            var req = new ReqResisterClient
            {                
            };

            if (!netMng.BeginSendJSON(req))
            {
                Logger.WriteLine("[GASensorClient] Failed to send ReqResisterClient.");
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
    }
}
