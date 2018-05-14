using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using GASensorClient;
using System.Web.Script.Serialization;

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
            msgWnd = new MsgWnd(OnShot);

            // 네트워크 매니저 생성
            netMng = new NetManager(this);

            //  서버 접속 시도
            if (!netMng.BeginConnect())
            {
                Logger.WriteLine("[GASensorClient] Failed to connect NetManager!");
                return;
            }
        }
    }
}
