package com.golfzon.cloudgs.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class SocketWrapper {
    public static final int NET_EVENT_CONNECTED = 0;
    public static final int NET_EVENT_DISCONNECT = 1;
    public static final int NET_EVENT_CONNECTING = 2;
    public static final int NET_EVENT_DATA_RECEIVED = 3;

    private Socket m_socket;
    private BufferedReader m_networkReader;
    private BufferedWriter m_networkWriter;

    private Handler m_exHandler;
    private final NetHandler m_inHandler = new NetHandler(this );
    // 핸들러 객체 만들기
    private static class NetHandler extends Handler {
        private final WeakReference<SocketWrapper> m_refs;
        public NetHandler(SocketWrapper obj) {
            m_refs = new WeakReference<SocketWrapper>(obj);
        }

        @Override
        public void handleMessage(Message msg) {
            SocketWrapper obj = m_refs.get();
            if (obj != null) obj.handleMessage(msg);
        }
    }
    private void handleMessage(Message msg) {
        if(m_exHandler == null) {
            Log.d("ga_log", "SocketWrapper handleMessage: m_exHandler == null");
            return;
        }

        m_exHandler.handleMessage( msg );
    }

    SocketWrapper(final String IP, final int PORT) {
        new Thread() {
            public void run() {
                try {
                    m_inHandler.obtainMessage(NET_EVENT_CONNECTING).sendToTarget();

                    m_socket = new Socket(IP, PORT);
                    if(m_socket.isConnected()) {
                        m_networkWriter = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream()));
                        m_networkReader = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));

                        m_inHandler.obtainMessage(NET_EVENT_CONNECTED).sendToTarget();

                        String line;
                        while (true) {
                            line = m_networkReader.readLine();

                            if (line.length() <= 0) {
                                Log.d("ga_log", "CheckReceived line == null");
                                m_inHandler.obtainMessage(NET_EVENT_DISCONNECT).sendToTarget();
                                break;
                            }

                            m_inHandler.obtainMessage(NET_EVENT_DATA_RECEIVED, line.length(), -1, line).sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    Log.d("ga_log", "SocketWrapper: " + e.getMessage());

                    m_inHandler.obtainMessage(NET_EVENT_DISCONNECT).sendToTarget();
                }
            }
        }.start();
    }

    public void SetHandler(Handler exHandler ) {
        m_exHandler = exHandler;
    }

    public void Send(String msg) {
        try {
            m_networkWriter.write(msg);
            m_networkWriter.flush();
        } catch (Exception e) {
            Log.d("ga_log", "SocketWrapper Send : " + e.getMessage());
        }
    }

    public void Close() {
        if(m_socket == null || m_socket.isClosed())
            return;

        try {
            m_socket.close();
        } catch (Exception e) {
            Log.d("ga_log", "SocketWrapper Close: " + e.getMessage());
        }
    }
}
