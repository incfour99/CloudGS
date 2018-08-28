package com.golfzon.cloudgs.bluetooth.hardware;

public interface HardwareInterface {
    String GetDeviceName();
    String GetStartEndSig();
    boolean ProcRawData(byte[] buffer, int bytes);
}
