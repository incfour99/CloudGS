package com.golfzon.cloudgs.network;

import com.google.gson.Gson;

import java.io.Serializable;

public class Packets {
    public static class PacketBase
    {
        public String header = "invald";

        public PacketBase()
        {
            header = this.getClass().getSimpleName();
        }
    }

    public static class ReqBase extends PacketBase
    {
        public int clientVersion = 0;
        public int packetVersion = 0;
    }

    public static class AckBase extends PacketBase
    {
        public int result = 0;
    }

    public static class ReqSeverPolicy extends ReqBase
    {
        public int dummy = 0;
    }

    public static class AckServerPolicy extends AckBase
    {
        public float expireTime = 0.0f;
    }

    public static class ReqGAServerInfo extends ReqBase
    {
        public int dummy = 0;
    }

    public static class AckGAServerInfo extends AckBase
    {
        public String ip = "";
        public String port = "";
    }

    public static class ReqShot extends ReqBase
    {
        public float ballSpeed = 0.0f;
        public float ballIncidence = 0.0f;
        public float ballDir = 0.0f;
        public float backSpin = 0.0f;
        public float sideSpin = 0.0f;
        // 추가 필요
    }

    public static class AckShot extends AckBase
    {
    }
}
