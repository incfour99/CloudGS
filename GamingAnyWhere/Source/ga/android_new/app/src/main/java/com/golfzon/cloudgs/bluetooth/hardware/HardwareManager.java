package com.golfzon.cloudgs.bluetooth.hardware;

import com.golfzon.cloudgs.bluetooth.hardware.device.BluetoothSWT;

public class HardwareManager implements HardwareInterface {
    public static final int CADDY_TALK = 0;
    public static final int SWING_TALK = 1;

    // Singleton interface
    private static HardwareManager instance;
    public synchronized static final HardwareManager Instance() {
        if (instance == null) {
            instance = new HardwareManager();
        }
        return instance;
    }

    private HardwareInterface m_device;

    HardwareManager() {
        // default SWT
        m_device = new BluetoothSWT();
    }

    public void SetDeviceType(int Type) {
        switch (Type) {
            case CADDY_TALK:
            case SWING_TALK:
            default:
                m_device = new BluetoothSWT();
                break;
        }

    }

    @Override
    public String GetDeviceName() {
        return m_device.GetDeviceName();
    }

    @Override
    public String GetStartEndSig() {
        return m_device.GetStartEndSig();
    }

    @Override
    public boolean ProcRawData(byte[] buffer, int bytes) {
        return m_device.ProcRawData(buffer, bytes);
    }
}
