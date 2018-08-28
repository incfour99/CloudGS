package com.golfzon.cloudgs.bluetooth.hardware.device;

import android.util.Log;

import com.golfzon.cloudgs.bluetooth.hardware.HardwareInterface;

public class BluetoothSWT implements HardwareInterface {

    private final static String DEVICE_NAME = "SwingTalk";
    private final static String SENSOR_SIGNAL = "#or";
    private final static int SENSOR_DATA_MIN_SIZE = 17; // 17 byte
    private static short m_index = 0;

    private final static int SENSOR_DATA_SIZE = 2000;
    private float[] m_timeData = new float[SENSOR_DATA_SIZE];
    private float[][] m_gyroData = new float[SENSOR_DATA_SIZE][3];
    private float[][] m_accData = new float[SENSOR_DATA_SIZE][3];
    private int m_impactCheckN = 0;
    private boolean m_bigData_n = false;

    @Override
    public String GetDeviceName() {
        return DEVICE_NAME;
    }

    @Override
    public String GetStartEndSig() {
        return SENSOR_SIGNAL;
    }

    // THOR 프로젝트에서 가져옴. 하드코딩된 수치값들이 있으나 아직 해석 못 함
    @Override
    public boolean ProcRawData(byte[] buffer, int bytes) {
        if(SENSOR_DATA_MIN_SIZE >= bytes)
            return false;

        boolean bShot = false;

        double DTime = 0.;
        double DGyroX = 0, DGyroY = 0, DGyroZ = 0;
        double DAccX = 0, DAccY = 0, DAccZ = 0;
        short SAccX = 0, SAccY = 0, SAccZ = 0;
        short SGyroX = 0, SGyroY = 0, SGyroZ = 0;

        int fin_count_s=0, fin_count_e=0;

        for(int count=0; count<(bytes-17); count++)
        {
            if (buffer[count] == '$' && buffer[count + 1] == 's' && buffer[count + 16] == '#' && buffer[count + 17] == 'E')
            {
                DTime = buffer[count + 2] << 8 | buffer[count + 3];

                DGyroX = buffer[count + 5] | buffer[count + 4] << 8;
                DGyroY = buffer[count + 7] | buffer[count + 6] << 8;
                DGyroZ = buffer[count + 9] | buffer[count + 8] << 8;

                DAccX = buffer[count + 11] | buffer[count + 10] << 8;
                DAccY = buffer[count + 13] | buffer[count + 12] << 8;
                DAccZ = buffer[count + 15] | buffer[count + 14] << 8;

                SAccX = (short)DAccX;
                SAccY = (short)DAccY;
                SAccZ = (short)DAccZ;

                SGyroX = (short)DGyroX;
                SGyroY = (short)DGyroY;
                SGyroZ = (short)DGyroZ;

                m_timeData[m_index]=(float)DTime;

                m_accData[m_index][0]=SAccX;
                m_accData[m_index][1]=SAccY;
                m_accData[m_index][2]=SAccZ;

                m_gyroData[m_index][0]=SGyroX;
                m_gyroData[m_index][1]=SGyroY;
                m_gyroData[m_index][2]=SGyroZ;

                if(m_impactCheckN < 0)
                {
                    if( (SGyroX<-2000) && (SGyroY>6000) )
                    {
                        m_impactCheckN = m_index + 200;
                        if(m_impactCheckN >= 2000)
                        {
                            m_impactCheckN = m_impactCheckN - 2000;
                        }
                    }
                }

                if(m_impactCheckN == m_index)
                {
                    m_impactCheckN =-1;

                    Log.d("ga_log", "DTime : " + DTime +
                            " SAccX : " + SAccX + " SAccY : " + SAccY + " SAccZ : " + SAccZ +
                            " SGyroX : " + SGyroX + " SGyroY : " + SGyroY + " SGyroZ : " + SGyroZ );

                    bShot = true;
                }

                m_index++;
                if(m_index >= SENSOR_DATA_SIZE)
                {
                    m_index =0;
                    m_bigData_n =true;
                }

                fin_count_s=count+18 ;
                fin_count_e=bytes;
            }
        }

        if(fin_count_e>fin_count_s)
        {
            int n_num=0;
            for(int i=fin_count_s; i<fin_count_e; i++)
            {
                buffer[n_num] = buffer[i];
                n_num++;
            }
        }

        return bShot;
    }
}
