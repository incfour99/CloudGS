using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace GASensorClient
{
    public static class Logger
    {        
        private static string LogFile = Environment.CurrentDirectory + "\\Log_" + DateTime.Now.ToString("yyyy_MM_dd_hh_mm") + ".txt";        
        public static bool bShowLogToConsole = true;

        public static void WriteLine(string txt)
        {
            try
            {
                File.AppendAllText(LogFile, DateTime.Now.ToString() + ": " + txt + "\r\n");
                if (bShowLogToConsole)
                    Console.WriteLine(txt);
            }
            catch (Exception e)
            {
                Console.WriteLine("[Logger] WriteLine error. Msg: " + e.Message);
            }
        }

        public static void DeleteLog()
        {
            File.Delete(LogFile);
        }
    }
}
