using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace GASensorClient
{
    public static class Logger
    {        
        private static string logFile = Environment.CurrentDirectory + "\\Log_" + DateTime.Now.ToString("yyyy_MM_dd_hh_mm_ss") + ".txt";        
        public static bool bShowLogToConsole = true;

        public static void WriteLine(string txt)
        {
            try
            {
                using (var fs = new FileStream(logFile, FileMode.Append, FileAccess.Write, FileShare.ReadWrite))
                {                    
                    using (var sr = new StreamWriter(fs))
                    {
                        sr.Write(DateTime.Now.ToString() + ": " + txt + "\r\n");
                        if (bShowLogToConsole)
                            Console.WriteLine(txt);
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("[Logger] WriteLine error. Msg: " + e.Message);
            }
        }

        public static void DeleteLog()
        {
            File.Delete(logFile);
        }
    }
}
