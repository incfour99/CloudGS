package com.golfzon.cloudgs.network;

public class Packets {
    public class PacketBase
    {
        public String header = "invald";

        public PacketBase()
        {
            header = this.getClass().getName();
        }
    }

    public class ReqBase extends PacketBase
    {
        public int clientVersion = 0;
        public int packetVersion = 0;
    }

    public class AckBase extends PacketBase
    {
        public int result = 0;
    }

    public class ReqSeverPolicy extends ReqBase
    {
        public int dummy = 0;
    }

    public class AckServerPolicy extends AckBase
    {
        public float expireTime = 0.0f;
    }

    public class ReqGAServerInfo extends ReqBase
    {
        public int dummy = 0;
    }

    public class AckGAServerInfo extends AckBase
    {
        public String ip = "";
        public String port = "";
    }

    public class ReqShot extends ReqBase
    {
        public float ballSpeed = 0.0f;
        public float ballIncidence = 0.0f;
        public float ballDir = 0.0f;
        public float backSpin = 0.0f;
        public float sideSpin = 0.0f;
        // 추가 필요
    }

    public class AckShot extends AckBase
    {
    }
}
