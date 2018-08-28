/*
 * Copyright (c) 2013 Chun-Ying Huang
 *
 * This file is part of GamingAnywhere (GA).
 *
 * GA is free software; you can redistribute it and/or modify it
 * under the terms of the 3-clause BSD License as published by the
 * Free Software Foundation: http://directory.fsf.org/wiki/License:BSD_3Clause
 *
 * GA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the 3-clause BSD License along with GA;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.gaminganywhere.gaclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.golfzon.cloudgs.activity.BluetoothActivity;
import com.golfzon.cloudgs.bluetooth.BluetoothService;
import com.golfzon.cloudgs.network.NetworkManager;
import com.golfzon.cloudgs.network.Packets;

import java.lang.ref.WeakReference;

import static com.golfzon.cloudgs.network.SocketWrapper.*;
import static com.golfzon.cloudgs.pref.CloudGSPref.*;

public class MainActivity extends Activity {
	private Button m_btnStartConnect = null;
	private TextView m_txtSensorStatus = null;
	private TextView m_txtServerStatus = null;
	private TextView m_txtProfile = null;

	class NetworkHandler extends Handler {
	    @Override
        public void handleMessage(Message msg) {
	        super.handleMessage(msg);

	        switch (msg.what) {
                case NET_EVENT_CONNECTED:
                    // ReqServerPolicy
                    Packets.ReqSeverPolicy req = new Packets.ReqSeverPolicy();
                    req.dummy = 0;
                    NetworkManager.Instance().Send( req );
                    UpdateUI();
                    break;
                case NET_EVENT_DISCONNECT:
                case NET_EVENT_CONNECTING:
                    UpdateUI();
                    break;
            }
        }
    };
    NetworkHandler m_netHandler = new NetworkHandler();

    private final BLTHandler m_bltHandler = new BLTHandler(this);
    private static class BLTHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        public BLTHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {

                activity.handleBltMessage(msg);
            }
        }
    }

    private void handleBltMessage(Message msg) {
        UpdateUI();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// connect button
		m_btnStartConnect = (Button) findViewById(R.id.btnStartConnect);
		m_btnStartConnect.setOnClickListener(new Button.OnClickListener() {
			@Override public void onClick(View view) {
				Connect();
			}
		});

		// server textview
		m_txtServerStatus = (TextView) findViewById(R.id.title_server_value);

		// sensor textview
		m_txtSensorStatus = (TextView) findViewById(R.id.title_sensor_status_value);

		// profile textview
		m_txtProfile = (TextView) findViewById(R.id.title_profile_value);

        BluetoothService blt = BluetoothService.Instance();
        blt.AddHandler(m_bltHandler);
        blt.ConnectToPairedDevice();

		NetworkManager.Instance().AddHandler(m_netHandler);
		NetworkManager.Instance().Connect();

		UpdateUI();
	}

	private void Connect() {
		SharedPreferences prefs = getSharedPreferences(CLOUD_GS_PREF_NAME, MODE_PRIVATE);
		String profileName = prefs.getString(CLOUD_GS_PREF_PROFILE_NAME, DEFAULT_PREF_PROFILE_NAME);
		boolean bUseBuiltInAudio = prefs.getBoolean(CLOUD_GS_PREF_USE_BUILT_IN_AUDIO, DEFAULT_PREF_USE_BUILT_IN_AUDIO);
		boolean bUseBulitInVideo = prefs.getBoolean(CLOUD_GS_PREF_USE_BUILT_IN_VIDEO, DEFAULT_PREF_USE_BUILT_IN_VIDEO);
		boolean bUseProraitMode = prefs.getBoolean(CLOUD_GS_PREF_USE_PORTRAIT_MODE, DEFAULT_PREF_USE_PORTRAIT_MODE);
		String controllerName = prefs.getString(CLOUD_GS_PREF_CONTROLLER_NAME, DEFAULT_PREF_CONTROLLER_NAME);
		int dropLateVFrame = prefs.getInt(CLOUD_GS_PREF_DROP_LATE_V_FRAME, DEFAULT_PREF_DROP_LATE_V_FRAME);
		int watchdogTimeout = prefs.getInt(CLOUD_GS_PREF_WATCHDOG_TIMEOUT, DEFAULT_PREF_WATCHDOG_TIMEOUT);

		Intent intent = new Intent(MainActivity.this, GAPlayerActivity.class );
		intent.putExtra("profile", profileName);
		intent.putExtra("builtinAudio", bUseBuiltInAudio);
		intent.putExtra("builtinVideo", bUseBulitInVideo);
		intent.putExtra("portraitMode", bUseProraitMode);
		intent.putExtra("controller", controllerName);
		intent.putExtra("dropLateVFrame", dropLateVFrame);
		intent.putExtra("watchdogTimeout", watchdogTimeout);
		startActivityForResult(intent, 0);
	}

	private void UpdateUI() {
		// Server status
		int serverStatus = NetworkManager.Instance().GetState();
		if(serverStatus == NET_EVENT_CONNECTED) {
            m_txtServerStatus.setText(R.string.connected);
            m_btnStartConnect.setEnabled(true);
        }
		else {
            m_txtServerStatus.setText(R.string.disconnected);
            m_btnStartConnect.setEnabled(false);
        }

		// Bluetooth status
		int bluetoothStatus = BluetoothService.Instance().GetState();
		if(bluetoothStatus == BluetoothService.BLT_CONNECTED)
			m_txtSensorStatus.setText(R.string.connected);
		else
			m_txtSensorStatus.setText(R.string.disconnected);

		SharedPreferences prefs = getSharedPreferences(CLOUD_GS_PREF_NAME, MODE_PRIVATE);
		m_txtProfile.setText(prefs.getString(CLOUD_GS_PREF_PROFILE_NAME, DEFAULT_PREF_PROFILE_NAME));
	}

    @Override
    protected void onResume() {
	    super.onResume();
	    UpdateUI();
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
			case R.id.menu_action_bluetooth:
				openBluetoothActivity();
				break;
			case R.id.menu_action_profile:
				openProfileActivity();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openBluetoothActivity() {
		Intent intent = null;
		intent = new Intent(MainActivity.this, BluetoothActivity.class);

		startActivityForResult(intent, 0);
	}

	private void openProfileActivity() {
		Intent intent = null;
		intent = new Intent(MainActivity.this, GASettingsActivity.class);

		startActivityForResult(intent, 0);
	}
}