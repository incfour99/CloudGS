package org.gaminganywhere.gaclient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.gaminganywhere.gaclient.BluetoothService.MESSAGE_BLUETOOTH_DISABLED;
import static org.gaminganywhere.gaclient.BluetoothService.MESSAGE_CONNECTED;
import static org.gaminganywhere.gaclient.BluetoothService.MESSAGE_CONNECTING;
import static org.gaminganywhere.gaclient.BluetoothService.MESSAGE_DISCONNECTED;
import static org.gaminganywhere.gaclient.BluetoothService.MESSAGE_READ;

public class BluetoothActivity extends Activity {
    public final static int REQUEST_ENABLE_BT = 1;

    // UI
    private TextView m_txtConnStatus;
    private Button m_btnCheckBluetooth;

    private BluetoothService m_bluetoothService;

    private final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MESSAGE_READ:
                    break;
                case MESSAGE_CONNECTED:
                case MESSAGE_CONNECTING:
                case MESSAGE_DISCONNECTED:
                    UpdateUI();
                    break;
                case MESSAGE_BLUETOOTH_DISABLED:
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        m_txtConnStatus = (TextView) findViewById(R.id.txtConnStatus);
        m_btnCheckBluetooth = (Button) findViewById(R.id.btnCheck);

        SetEventListeners();

        BluetoothService blt = BluetoothService.getInstance();
        blt.SetHandler(m_handler);
        blt.ConnectToPairedDevice();
    }

    void SetEventListeners() {
        // 연결 버튼
        m_btnCheckBluetooth.setOnClickListener( new Button.OnClickListener() {
            @Override public void onClick(View view) {
                BluetoothService blt = BluetoothService.getInstance();
                blt.ConnectToPairedDevice();
            }
        }) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BluetoothService blt = BluetoothService.getInstance();
                blt.ConnectToPairedDevice();
            }
            else {
                m_txtConnStatus.setText("Disconnected");
            }
        }
    }

    private void onConnectionLost() {
        UpdateUI();
    }

    private void UpdateUI() {
        BluetoothService blt = BluetoothService.getInstance();
        switch(blt.getState())
        {
            case BluetoothService.BLT_STATE_DISCONNECTED:
                m_txtConnStatus.setText(R.string.disconnected);
                m_btnCheckBluetooth.setEnabled(true);

                break;
            case BluetoothService.BLT_STATE_CONNECTED:
                m_txtConnStatus.setText(R.string.connected);
                m_btnCheckBluetooth.setEnabled(false);

                break;
            case BluetoothService.BLT_STATE_CONNECTING:
                m_txtConnStatus.setText(R.string.connecting);
                m_btnCheckBluetooth.setEnabled(false);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d("ga_log", "onBackPressed: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ga_log", "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("ga_log", "onDestroy: ");
    }
}
