using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace GASensorClient
{
    partial class GASensorClient
    {
        public void OnAckResisterClient(string message)
        {
            AckResisterClient ack = NetUtils.DeserializeJSON<AckResisterClient>(message);

            if (ack.result != 1)
            {
                Logger.WriteLine("[RecvDataHandler] AckResisterClient result fail. result code: " + ack.result);
                return;
            }
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
