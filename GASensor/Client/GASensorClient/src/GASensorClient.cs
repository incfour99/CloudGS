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

        public void Start()
        {
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
