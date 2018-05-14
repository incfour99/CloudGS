using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace GASensorClient
{
    delegate void OnSensorEvent(ShotData shotData);
    class MsgWnd
    {
        private CustomWindow msgWnd;
        private const int SENSOR_EVENT = 200;

        private const string sensorFile = @"c:\shotdataCD.txt";

        private OnSensorEvent listener;

        public MsgWnd(OnSensorEvent li)
        {
            listener = li;

            List<int> events = new List<int>();
            events.Add(SENSOR_EVENT);

            msgWnd = new CustomWindow("MsgWnd", OnSignal, events);
        }

        private void OnSignal(uint msg, IntPtr wParam, IntPtr lParam)
        {
            if (msg == SENSOR_EVENT)
            {
                ShotData data = ReadSensorFile();

                if(listener != null)
                    listener(data);
            }
        }

        private ShotData ReadSensorFile()
        {
            ShotData sData = new ShotData();

            try
            {
                using (var fs = new FileStream(sensorFile, FileMode.Open, FileAccess.Read, FileShare.ReadWrite))
                using (var sr = new StreamReader(fs, Encoding.Default))
                {
                    string text = sr.ReadToEnd();
                    int[] nums = text.Split(',').Select(int.Parse).ToArray();

                    if (nums.Length == 5)
                    {
                        sData.ballSpeed = nums[0];
                    }
                    else
                    {
                        throw new System.Exception("[MsgWnd] ReadSensorFile sensor data is not enough. cnt : " + nums.Length);
                    }
                }
            }
            catch(Exception e)
            {
                Logger.WriteLine("[MsgWnd] ReadSensorFile Failed to read & parse sensor file. Msg: " + e.Message);
                return sData;
            }

            return sData;
        }
    }
}
