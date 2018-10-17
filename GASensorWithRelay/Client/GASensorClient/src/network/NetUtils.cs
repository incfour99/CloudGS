using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.Script.Serialization;

namespace GASensorClient
{
    class NetUtils
    {
        public static string SerializeJSON(object obj)
        {
            return new JavaScriptSerializer().Serialize(obj);
        }

        public static T DeserializeJSON<T>(string message)
        {
            T ack = new JavaScriptSerializer().Deserialize<T>(message);
            return ack;
        }

        public static string GetHeader(string obj)
        {
            AckBase ack = new JavaScriptSerializer().Deserialize<AckBase>(obj);

            return ack.header;
        }
    }
}
