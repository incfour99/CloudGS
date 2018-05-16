using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace GASensorClient
{
    abstract class INetManager
    {
        abstract public void OnConnectServer(bool isError, string message);
        abstract public void OnReceiveData(bool isError, string message);        
    }

    class NetManager
    {
        //private string HOST = "127.0.0.1"; // LocalHost
        private string HOST = "176.32.72.169"; // 일본 도쿄
        //private string HOST = "34.235.58.175"; // 미국 버지니아
        
        private int PORT = 9000;
        private const int MAXSIZE = 4096;   /* 4096  */

        private Socket clientSock;  /* client Socket */
        private Socket cbSock;   /* client Async Callback Socket */
        private byte[] recvBuffer;

        private List<INetManager> listeners = new List<INetManager>();

        public NetManager(INetManager listener)
        {
            listeners.Add(listener);

            recvBuffer = new byte[MAXSIZE];
            clientSock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        }

        public bool IsConnected()
        {
            return clientSock.Connected;
        }

        public bool BeginConnect()
        {            
            try
            {
                if (IsConnected())
                    return true;

                clientSock.BeginConnect(HOST, PORT, new AsyncCallback(ConnectCallBack), clientSock);
                Logger.WriteLine("[NetManager] BeginConnect! HOST : " + HOST + " PORT : " + PORT);
            }
            catch (SocketException se)
            {
                Logger.WriteLine("[NetManager] ERROR : Connection could not be established. Msg: " + se.Message);
                return false;
            }

            return true;
        }

        private void ConnectCallBack(IAsyncResult IAR)
        {
            try
            {
                Logger.WriteLine("[NetManager] Connection is established.");

                Socket tempSock = (Socket)IAR.AsyncState;
                IPEndPoint svrEP = (IPEndPoint)tempSock.RemoteEndPoint;                
                tempSock.EndConnect(IAR);

                cbSock = tempSock;
                
                foreach (var listener in listeners)
                    listener.OnConnectServer(false, "");
                
                cbSock.BeginReceive(this.recvBuffer, 0, recvBuffer.Length, SocketFlags.None, new AsyncCallback(OnReceiveCallBack), cbSock);
            }
            catch (SocketException se)
            {
                foreach (var listener in listeners)
                    listener.OnConnectServer(true, se.Message);                

                if (se.SocketErrorCode == SocketError.NotConnected)
                {                    
                    Logger.WriteLine("[NetManager] ERROR : Connection could not be established. Msg: " + se.Message);

                    this.BeginConnect();
                }
            }
        }

        public bool BeginSendJSON(object obj)
        {
            string json = NetUtils.SerializeJSON(obj);

            return BeginSend(json);            
        }

        public bool BeginSend(string message)
        {
            try
            {
                if (clientSock.Connected)
                {
                    byte[] buffer = new UTF8Encoding().GetBytes(message);
                    
                    clientSock.BeginSend(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(OnSendCallBack), message);
                    Logger.WriteLine("[NetManager] BeginSend Succeed!");
                }
            }
            catch (SocketException se)
            {
                Logger.WriteLine("[NetManager] ERROR : Failed to send. Msg: " + se.Message);
                return false;
            }

            return true;
        }

        private void OnSendCallBack(IAsyncResult IAR)
        {
            if (IAR == null)
            {
                Logger.WriteLine("[NetManager] OnSendCallBack IAR is null!");
                return;
            }
            string message = (string)IAR.AsyncState;

            Logger.WriteLine("[NetManager] OnSendCallBack: " + message);
        }

        private void OnReceiveCallBack(IAsyncResult IAR)
        {
            try
            {
                Socket tempSock = (Socket)IAR.AsyncState;

                int nReadSize = tempSock.EndReceive(IAR);

                if (nReadSize != 0)
                {
                    string message = new UTF8Encoding().GetString(recvBuffer, 0, nReadSize);

                    Logger.WriteLine("[NetManager] Received: " + message);

                    foreach (var listener in listeners)
                        listener.OnReceiveData(false, message);
                }

                cbSock.BeginReceive(this.recvBuffer, 0, recvBuffer.Length, SocketFlags.None, new AsyncCallback(OnReceiveCallBack), cbSock);
            }
            catch (SocketException se)
            {
                foreach (var listener in listeners)
                    listener.OnReceiveData(true, se.Message);
                
                if (se.SocketErrorCode == SocketError.ConnectionReset)
                {
                    Logger.WriteLine("[NetManager] ERROR : Failed to receive data from server. Msg: " + se.Message);

                    this.BeginConnect();
                }
            }
        }
    }
}
