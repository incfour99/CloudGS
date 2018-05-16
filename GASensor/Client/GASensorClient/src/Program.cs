using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace GASensorClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Logger.WriteLine("[Main] GASensorClient program is started. ");

            // gaSensorClient 생성 및 시작
            GASensorClient gaSensorClient = new GASensorClient();
            gaSensorClient.Start();

            NativeMethods.WinAPI.MSG msg = new NativeMethods.WinAPI.MSG();
            bool runForever = true;
            while (runForever)
            {
                //string message = Console.ReadLine();
                //if (message.Equals("quit"))
                //    break;

                //if (message.Length > 0)
                //    HandleCommand(message);
                
                if (NativeMethods.WinAPI.PeekMessage(ref msg, IntPtr.Zero, 0, 0, NativeMethods.WinAPI.PM_REMOVE))
                {
                    NativeMethods.WinAPI.TranslateMessage(ref msg);
                    NativeMethods.WinAPI.DispatchMessage(ref msg);
                }

                System.Threading.Thread.Sleep(16);
            }

            Logger.WriteLine("[Main] GASensorClient program is finished. Press Enter! ##");
            Console.ReadLine();
        }

        static void HandleCommand(string msg)
        {
            // 로직 간소화를 위해 소문자로 모두 변환
            msg = msg.ToLower();

            // 테스트샷 처리
            if(msg.Contains("testshot"))
            {
                string[] args = msg.Split(' ').ToArray<string>();
                if (args.Length != 6)
                    return;

                try
                {
                    string sensorFile = @"c:\shotdataCD.txt";
                    using (var fs = new FileStream(sensorFile, FileMode.Create, FileAccess.Write, FileShare.ReadWrite))
                    using (var sr = new StreamWriter(fs, Encoding.Default))
                    {
                        sr.Write(args[1] + "," + args[2] + "," + args[3] + "," + args[4] + "," + args[5]);
                        sr.Flush();
                    }

                    IntPtr handle = NativeMethods.WinAPI.FindWindow("MsgWnd", null);
                    if (handle != null)
                        NativeMethods.WinAPI.SendMessage(handle, 200, 0, 0);
                }
                catch (Exception e)
                {
                    Logger.WriteLine("[HandleCommand] Failed to Test shot. Msg: " + e.Message);                    
                }
            }
        }
    }
}
