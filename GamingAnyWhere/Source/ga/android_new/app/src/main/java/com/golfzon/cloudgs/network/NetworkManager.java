package com.golfzon.cloudgs.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.golfzon.cloudgs.network.SocketWrapper.NET_EVENT_CONNECTED;
import static com.golfzon.cloudgs.network.SocketWrapper.NET_EVENT_CONNECTING;
import static com.golfzon.cloudgs.network.SocketWrapper.NET_EVENT_DATA_RECEIVED;
import static com.golfzon.cloudgs.network.SocketWrapper.NET_EVENT_DISCONNECT;

public class NetworkManager {
    //private static final String HOST = "192.168.0.1"; // 로컬
    private static final String HOST = "176.32.72.169"; // 일본 도쿄
    //private static final String HOST = "34.235.58.175"; // 미국 버지니아
    private static final int PORT = 9000;

    private SocketWrapper m_sockWrapper;
    private int m_state = NET_EVENT_DISCONNECT;

    private ArrayList<Handler> m_exHandler = new ArrayList<Handler>();
    private final NetHandler m_handler = new NetHandler(this );
    // 핸들러 객체 만들기
    private static class NetHandler extends Handler {
        private final WeakReference<NetworkManager> m_refs;
        public NetHandler(NetworkManager obj) {
            m_refs = new WeakReference<NetworkManager>(obj);
        }

        @Override
        public void handleMessage(Message msg) {
            NetworkManager obj = m_refs.get();
            if (obj != null) obj.handleMessage(msg);
        }
    }

    private static final String PACKTETS_CLASS_PREFIX = Packets.class.getName();

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case NET_EVENT_CONNECTED:
            case NET_EVENT_DISCONNECT:
            case NET_EVENT_CONNECTING:
                SetState(msg.what);
                break;
            case NET_EVENT_DATA_RECEIVED:
                String buffer = (String)msg.obj;

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(buffer);

                if(element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    String header = obj.get("header").getAsString();

                    Gson gson = new Gson();
                    try {
                        String className = PACKTETS_CLASS_PREFIX + "$" + header;
                        Packets.AckBase ackBase = (Packets.AckBase)gson.fromJson(buffer, Class.forName(className));
                        OnReceived( ackBase );
                    } catch (Exception e) {
                        Log.d("ga_log", "NetworkManager handleMessage: " + e.getMessage());
                    }
                }
                break;
        }

        for(Handler h : m_exHandler) {
            h.handleMessage(msg);
        }
    }

    // Singleton interface
    private static NetworkManager instance;
    public synchronized static final NetworkManager Instance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    // state set
    private synchronized void SetState(int state) {
        Log.d("ga_log", "setState() " + m_state + " -> " + state);
        m_state = state;
    }

    // state get
    public synchronized int GetState() {
        return m_state;
    }

    public void AddHandler(Handler handler) {
        if(m_exHandler.contains(handler))
            return;

        m_exHandler.add(handler);
    }

    public void Connect() {
        if(m_sockWrapper == null) {
            m_sockWrapper = new SocketWrapper(HOST, PORT);
            m_sockWrapper.SetHandler(m_handler);
        }
    }

    public void Disconnect() {
        m_sockWrapper.Close();
    }

    public void Send(Packets.PacketBase packet) {
        Gson gson = new Gson();
        m_sockWrapper.Send( gson.toJson(packet) );
    }

    private void OnReceived(Packets.AckBase ackBase) {
        Log.d("ga_log", "OnReceived: " + ackBase);

        String ackName = ackBase.getClass().getSimpleName();

        // OnAckServerPolicy
        if(ackName == Packets.AckServerPolicy.class.getSimpleName()) {
            Packets.ReqGAServerInfo req = new Packets.ReqGAServerInfo();
            req.dummy = 0;

            NetworkManager.Instance().Send(req);
        }
        // OnAckGAServerInfo
        else if( ackName == Packets.AckGAServerInfo.class.getSimpleName()) {

        }
    }
}
