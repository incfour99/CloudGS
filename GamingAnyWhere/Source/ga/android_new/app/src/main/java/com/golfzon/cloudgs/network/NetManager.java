package com.golfzon.cloudgs.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class NetManager {
    private static final String HOST = "176.32.72.169"; // 일본 도쿄
    //private String HOST = "34.235.58.175"; // 미국 버지니아
    private static final int PORT = 9000;

    private static final int ON_DATA_RECEIVED = 0;

    private Socket m_socket;
    private BufferedReader m_networkReader;
    private BufferedWriter m_networkWriter;

    private final NetHandler m_handler = new NetHandler(this );
    // 핸들러 객체 만들기
    private static class NetHandler extends Handler {
        private final WeakReference<NetManager> m_refs;
        public NetHandler(NetManager obj) {
            m_refs = new WeakReference<NetManager>(obj);
        }

        @Override
        public void handleMessage(Message msg) {
            NetManager obj = m_refs.get();
            if (obj != null) obj.handleMessage(msg);
        }
    }
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case ON_DATA_RECEIVED:
                break;
            default:
                break;
        }
    }

    // Singleton interface
    private static NetManager instance;
    public synchronized static final NetManager getInstance() {
        if (instance == null) {
            instance = new NetManager();
        }
        return instance;
    }

    NetManager() {
        try {
            m_socket = new Socket(HOST, PORT);
            m_networkWriter = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream()));
            m_networkReader = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));

            CheckReceived.start();
        } catch (IOException e1) {
            Log.d("ga_log", "NetManager: " + e1.getMessage());
        }
    }

    private Thread CheckReceived = new Thread() {
        public void run() {
            try {
                String buffer;
                while (true) {
                    buffer = m_networkReader.readLine();
                    m_handler.obtainMessage(ON_DATA_RECEIVED, buffer.length(), -1, buffer);
                }
            } catch (Exception e) {
                Log.d("ga_log", "CheckReceived : " + e.getMessage());
            }
        }
    };
}
