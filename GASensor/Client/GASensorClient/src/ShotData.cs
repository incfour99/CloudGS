using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GASensorClient
{
    class ShotData
    {
        public float ballSpeed = 0.0f;
        public float ballIncidence = 0.0f;
        public float ballDir = 0.0f;
        public float backSpin = 0.0f;
        public float sideSpin = 0.0f;

        public ShotData()
        {
        }

        public ShotData(float v1, float v2, float v3, float v4, float v5)
        {
            ballSpeed = v1; ballIncidence = v2; ballDir = v3; backSpin = v4; sideSpin = v5;
        }
    }
}
