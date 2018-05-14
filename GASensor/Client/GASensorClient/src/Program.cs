using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

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

            bool runForever = true;
            while (runForever)
            {
                string message = Console.ReadLine();
                if (message.Equals("quit"))
                    break;
            }

            Logger.WriteLine("[Main] GASensorClient program is finished. Press Enter! ##");
            Console.ReadLine();
        }
    }
}
