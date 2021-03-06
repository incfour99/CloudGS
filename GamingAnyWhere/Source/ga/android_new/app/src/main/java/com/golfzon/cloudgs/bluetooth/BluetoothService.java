package com.golfzon.cloudgs.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.golfzon.cloudgs.bluetooth.hardware.HardwareManager;
import com.golfzon.cloudgs.network.Packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    public static final int BLT_DISCONNECTED = 0;
    public static final int BLT_CONNECTED = 1;
    public static final int BLT_CONNECTING = 2;

    private static int m_state = BLT_DISCONNECTED;

    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int BLT_EVENT_READ = 0;
    public static final int BLT_EVENT_CONNECTED = 1;
    public static final int BLT_EVENT_CONNECTING = 2;
    public static final int BLT_EVENT_DISCONNECTED = 3;
    public static final int BLT_EVENT_DISABLED = 4;
    public static final int BLT_EVENT_GAME_SHOT = 5;

    private BluetoothAdapter m_bluetoothAdapter;
    private BluetoothSocket m_bluetoothConnSocket;
    private BluetoothDevice m_bluetoothDevice;

    private ArrayList<String> PairedDeviceNames = new ArrayList<String>();

    private ArrayList<Handler> m_exHandlers = new ArrayList<Handler>();
    private final BLTSHandler m_inHandler = new BLTSHandler(this );
    // 핸들러 객체 만들기
    private static class BLTSHandler extends Handler {
        private final WeakReference<BluetoothService> m_refs;
        public BLTSHandler(BluetoothService obj) {
            m_refs = new WeakReference<BluetoothService>(obj);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothService obj = m_refs.get();
            if (obj != null) obj.handleMessage(msg);
        }
    }

    // Singleton interface
    private static BluetoothService instance;
    public synchronized static final BluetoothService Instance() {
        if (instance == null) {
            instance = new BluetoothService();
        }
        return instance;
    }

    // Constructors
    public BluetoothService() {
        // BluetoothAdapter 얻기
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_bluetoothAdapter == null) {
            Log.d("ga_log","device does not support bluetooth.");
        }
    }

    public void AddHandler(Handler handle) {
        m_exHandlers.add(handle);
    }

    public void RemoveHandler(Handler handle) {
        m_exHandlers.remove(handle);
    }

    // Bluetooth 상태 set
    private synchronized void SetState(int state) {
        Log.d("ga_log", "setState() " + m_state + " -> " + state);
        m_state = state;
    }

    // Bluetooth 상태 get
    public synchronized int GetState() {
        return m_state;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID_SPP);
            } catch (IOException e) {
                Log.d("ga_log", Log.getStackTraceString(e.getCause().getCause()));
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            m_bluetoothAdapter.cancelDiscovery();

            if(mmSocket == null)
                Log.d("ga_log", "mmSoket is null");

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d("ga_log", "Unable to connect; close the socket and get out. " + connectException.getMessage() );

                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d("ga_log", "Failed to close");
                }
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

        private void manageConnectedSocket(BluetoothSocket socket) {
            if(socket.isConnected())
                m_inHandler.obtainMessage(BLT_EVENT_CONNECTED, socket).sendToTarget();
            else
                m_inHandler.obtainMessage(BLT_EVENT_DISCONNECTED).sendToTarget();
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("ga_log", "ConnectedThread run start");

            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    m_inHandler.obtainMessage(BLT_EVENT_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    m_inHandler.obtainMessage(BLT_EVENT_DISCONNECTED).sendToTarget();
                    Log.d("ga_log", "ConnectedThread connection lost. " + e.getMessage());
                    break;
                }
            }
        }

        /* Call this from the menu_main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the menu_main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void ConnectToPairedDevice() {
        Log.d("ga_log", "BluetoothActivity::ConnectToPairedDevice start");

        Log.d("ga_log", "m_bluetoothAdapter : " + m_bluetoothAdapter);
        Log.d("ga_log", "m_bluetoothDevice : " + m_bluetoothDevice);
        Log.d("ga_log", "m_bluetoothConnSocket : " + m_bluetoothConnSocket);

        if (m_bluetoothAdapter == null) {
            Log.d("ga_log","device does not support bluetooth.");
            return;
        }

        if (!m_bluetoothAdapter.isEnabled()) {
            Log.d("ga_log", "bluetooth is disabled.");
            m_inHandler.obtainMessage(BLT_EVENT_DISABLED).sendToTarget();
            return;
        }
        else {
            Log.d("ga_log", "bluetooth is enabled.");
        }

        if(m_bluetoothConnSocket != null && m_bluetoothConnSocket.isConnected()) {
            Log.d("ga_log", "already connected ");
            m_inHandler.obtainMessage(BLT_EVENT_CONNECTED, m_bluetoothConnSocket).sendToTarget();
            return;
        }

        String deviceName;
        // 페어링된 장비를 찾음
        Set<BluetoothDevice> pairedDevices = m_bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.d("ga_log", "The nums of paired devices is " + pairedDevices.size());

            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();

                // Add the name and address to an array adapter to show in a ListView
                PairedDeviceNames.add(device.getName());
                Log.d("ga_log", "PairedDevice : " + device.getName());

                String currDeviceName = HardwareManager.Instance().GetDeviceName();
                Log.d("ga_log", "Set Device : " + currDeviceName);
                if(deviceName.equals(currDeviceName)) {
                    m_bluetoothDevice = device;
                    ConnectThread thread = new ConnectThread(device);
                    thread.start();

                    SetState(BLT_CONNECTING);
                    m_inHandler.obtainMessage(BLT_EVENT_CONNECTING).sendToTarget();
                }
            }
        }
    }

    public void SendReadySignal() {
        try {
            String startSig = HardwareManager.Instance().GetStartEndSig();

            OutputStream oStream = m_bluetoothConnSocket.getOutputStream();
            oStream.write(startSig.getBytes());

            Log.d("ga_log", "SendReadySignal signal sended : " + startSig);
        } catch (Exception e) {
            Log.d("ga_log", "SendReadySignal Exception : " + e.getMessage());
        }
    }

    // Handler 에서 호출하는 함수
    private void handleMessage(Message msg) {
        boolean bSendMsg = true;

        switch (msg.what) {
            case BLT_EVENT_READ:
                bSendMsg = HardwareManager.Instance().ProcRawData((byte[])msg.obj, msg.arg1);
                if(bSendMsg) {
                    msg.what = BLT_EVENT_GAME_SHOT;
                }
                break;
            case BLT_EVENT_CONNECTED:
                Log.d("ga_log", "BLT_EVENT_CONNECTED MESSAGE : " + msg.what);

                SetState(BLT_CONNECTED);
                m_bluetoothConnSocket = (BluetoothSocket)msg.obj;

                ConnectedThread thread = new ConnectedThread(m_bluetoothConnSocket);
                thread.start();
                break;
            case BLT_EVENT_DISCONNECTED:
                Log.d("ga_log", "BLT_EVENT_DISCONNECTED MESSAGE : " + msg.what);

                try {
                    m_bluetoothConnSocket.close();
                    m_bluetoothDevice = null;
                } catch (Exception e) {
                    Log.d("ga_log", "BLT_EVENT_CONNECTED Failed to close : " + e.getMessage());
                }


                SetState(BLT_DISCONNECTED);
                break;
            default:
                Log.d("ga_log", "MESSAGE : " + msg.what);
                break;
        }

        if(!bSendMsg)
            return;

        for(Handler h : m_exHandlers) {
            if(h != null)
                h.obtainMessage(msg.what).sendToTarget();
        }
    }
}
