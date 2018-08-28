package com.golfzon.cloudgs.bluetooth.hardware.device;

import com.golfzon.cloudgs.bluetooth.hardware.HardwareInterface;

public class BluetoothCDT implements HardwareInterface {
    private final static String DEVICE_NAME = "CaddieTalk";

    @Override
    public String GetDeviceName() {
        return null;
    }

    @Override
    public String GetStartEndSig() {
        return null;
    }

    @Override
    public boolean ProcRawData(byte[] buffer, int bytes) {
        return false;
    }
}
