using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GASensorClient
{
    class PacketBase
    {
        public string header = "invald";

        public PacketBase()
        {
            header = this.GetType().Name;
        }
    }

    class ReqBase : PacketBase
    {   
        public int clientVersion = 0;
        public int packetVersion = 0;        
    }

    class AckBase : PacketBase
    {
        public int result = 0;
    }

    class ReqResisterClient : ReqBase
    {     
    }

    class AckResisterClient : AckBase
    {        
    }

    class ReqShot : ReqBase
    {
        public float ballSpeed = 0.0f;
        public float ballIncidence = 0.0f;
        public float ballDir = 0.0f;
        public float backSpin = 0.0f;
        public float sideSpin = 0.0f; 
        // 추가 필요
    }

    class AckShot : AckBase
    {
    }
}
