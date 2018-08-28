package com.golfzon.cloudgs.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.golfzon.cloudgs.bluetooth.BluetoothService;

import org.gaminganywhere.gaclient.GASettingsActivity;
import org.gaminganywhere.gaclient.MainActivity;
import org.gaminganywhere.gaclient.R;

import java.lang.ref.WeakReference;

import static com.golfzon.cloudgs.bluetooth.BluetoothService.BLT_EVENT_DISABLED;
import static com.golfzon.cloudgs.bluetooth.BluetoothService.BLT_EVENT_CONNECTED;
import static com.golfzon.cloudgs.bluetooth.BluetoothService.BLT_EVENT_CONNECTING;
import static com.golfzon.cloudgs.bluetooth.BluetoothService.BLT_EVENT_DISCONNECTED;
import static com.golfzon.cloudgs.bluetooth.BluetoothService.BLT_EVENT_READ;

public class BluetoothActivity extends Activity {
    public final static int REQUEST_ENABLE_BT = 1;

    // UI
    private TextView m_txtConnStatus;
    private Button m_btnCheckBluetooth;

    // 핸들러 객체 만들기
    private final BLTHandler m_bltHandler = new BLTHandler(this);
    private static class BLTHandler extends Handler {
        private final WeakReference<BluetoothActivity> mActivity;
        public BLTHandler(BluetoothActivity activity) {
            mActivity = new WeakReference<BluetoothActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothActivity activity = mActivity.get();
            if (activity != null) {

                activity.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        m_txtConnStatus = (TextView) findViewById(R.id.txtConnStatus);
        m_btnCheckBluetooth = (Button) findViewById(R.id.btnCheck);

        SetEventListeners();

        BluetoothService blt = BluetoothService.Instance();
        blt.AddHandler(m_bltHandler);
        blt.ConnectToPairedDevice();
    }

    void SetEventListeners() {
        // 연결 버튼
        m_btnCheckBluetooth.setOnClickListener( new Button.OnClickListener() {
            @Override public void onClick(View view) {
                BluetoothService blt = BluetoothService.Instance();
                blt.ConnectToPairedDevice();
            }
        }) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                BluetoothService blt = BluetoothService.Instance();
                blt.ConnectToPairedDevice();
            }
            else {
                m_txtConnStatus.setText("Disconnected");
            }
        }
    }

    private void UpdateUI() {
        BluetoothService blt = BluetoothService.Instance();
        switch(blt.GetState()) {
            case BluetoothService.BLT_DISCONNECTED:
                m_txtConnStatus.setText(R.string.disconnected);
                m_btnCheckBluetooth.setEnabled(true);

                break;
            case BluetoothService.BLT_CONNECTED:
                m_txtConnStatus.setText(R.string.connected);
                m_btnCheckBluetooth.setEnabled(false);

                break;
            case BluetoothService.BLT_CONNECTING:
                m_txtConnStatus.setText(R.string.connecting);
                m_btnCheckBluetooth.setEnabled(false);
                break;
        }
    }

    // Handler 에서 호출하는 함수
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case BLT_EVENT_READ:
                break;
            case BLT_EVENT_CONNECTED:
            case BLT_EVENT_CONNECTING:
            case BLT_EVENT_DISCONNECTED:
                UpdateUI();
                break;
            case BLT_EVENT_DISABLED:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //lastSelection = spinner_profile.getSelectedItemPosition();
        switch(item.getItemId()) {
            case R.id.menu_action_home:
                goBackToHome();
                break;
            case R.id.menu_action_profile:
                openSettingActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        BluetoothService blt = BluetoothService.Instance();
        blt.RemoveHandler(m_bltHandler);

        super.onDestroy();

        Log.d("ga_log", "onDestroy: ");
    }

    private void goBackToHome() {
        Intent intent = new Intent( BluetoothActivity.this, MainActivity.class);

        startActivity(intent);
    }

    private void openSettingActivity() {
        Intent intent = new Intent(BluetoothActivity.this, GASettingsActivity.class);

        startActivity(intent);
    }
}
