package com.gtafe.carvideosys;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;


/**
 * Created by Administrator on 2015/6/18.
 */
public abstract class BaseActivity extends Activity {
//    private final static String TAG = byteTest.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public String mDeviceName;
    public String mDeviceAddress;
    public BluetoothLeService mBluetoothLeService;
    public boolean mConnected = false;
    public float initialX = 0, initialY = 0;
    public Util util;
    byte[] inputBuffer;

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    /**
     * 广播接收器 *
     */
    public BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // ??????
//                Log.e(TAG, "Only gatt, just wait");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) { // 断开连接
                mConnected = false;
                invalidateOptionsMenu();
                DisconDialog();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) // 发现服务
            {
                mConnected = true;
                ConDialog();
                // btnSend.setEnabled(true);
//                Log.e(TAG, "In what we need");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // 接收数据
//                Log.e(TAG, "RECV DATA");
                inputBuffer = intent
                        .getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String datas = util.byteToHexString(inputBuffer);
                getInfo(inputBuffer);
                Log.e("rec1", datas);
            }
        }
    };
    protected abstract void getInfo(byte[] inputBuffer);

    public void clearUI() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { // ?????
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection);
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createdMenu(menu, mConnected);
        return true;
    }


    public void createdMenu(Menu menu, boolean mConnected) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //
        connecteToBuletooth(item, mDeviceAddress);
        return super.onOptionsItemSelected(item);
    }

    public void connecteToBuletooth(MenuItem item, String mDeviceAddress) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                break;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                break;
            case android.R.id.home:
                if (mConnected) {
                    mBluetoothLeService.disconnect();
                    mConnected = false;
                }
                onBackPressed();
                break;
        }
    }

    private void ConDialog() {
        Toast.makeText(this, "建立连接", Toast.LENGTH_SHORT).show();
    }

    private void DisconDialog() {
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
    }

    public static IntentFilter makeGattUpdateIntentFilter() { //
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    public int getGesture(int action, MotionEvent event) {
        int gesture = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float finalY = event.getY();
//                Log.d(TAG, "Action was UP1---->" + (initialY - finalY));
                if (initialY - finalY < -50) {
//                    Log.d(TAG, "下---->" );
                    gesture = 2;
                } else if (initialY - finalY > 10) {
//                    Log.d(TAG, "上---->" );
                    gesture = 1;
                } else {
                    gesture = 0;
                }
                break;
        }
        return gesture;
    }

    ;


}

