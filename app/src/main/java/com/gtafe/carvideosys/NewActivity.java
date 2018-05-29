package com.gtafe.carvideosys;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by ZhouJF on 2018/4/21.
 */

public class NewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public View actionbarLayout;
    public List<String> mDec;
    public List<String> mVideo;
    public DecAdapter mDecAdapter;
    public SurfaceView mSrfaceView;
    public MediaPlayer mediaPlayer;
    public int position;
    private Util util = new Util();
    public String mDeviceName;
    public String mDeviceAddress;
    public BluetoothLeService mBluetoothLeService;
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
    public Button mAuto;
    public Button mStop;
    public Button mStart;
    public TextView mConnect;
    public ImageView mLoading;
    public SurfaceHolder mHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        setActionBarTitle("新能源汽车实训");
        mDec = new ArrayList<>();
        mVideo = new ArrayList<>();
        initFile();
        initView();
        initServer();
        initSurfaceview();
        initBrocast();
    }

    private void initServer() {
        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Log.e(TAG, "initServer: mDeviceName=" + mDeviceName + " mDeviceAddress=" + mDeviceAddress);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initSurfaceview() {
        mSrfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSrfaceView.getHolder();
        mediaPlayer = new MediaPlayer();

        // 设置SurfaceView自己不管理的缓冲区
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.e(TAG, "surfaceCreated: ");
                mediaPlayer.setDisplay(mHolder);
             //   play(i);

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.e(TAG, "surfaceChanged: ");
            }
        });
    }

    int i = 0;

    private void initView() {
        ListView listView = (ListView) findViewById(R.id.new_lv);

        listView.setDividerHeight(12);

        mDecAdapter = new DecAdapter();
        listView.setAdapter(mDecAdapter);


        mAuto = (Button) findViewById(R.id.auto);
        mStop = (Button) findViewById(R.id.stop);
        mStart = (Button) findViewById(R.id.start);
        mConnect = (TextView) findViewById(R.id.connect);
        mLoading = (ImageView) findViewById(R.id.connect_loading);

        mAuto.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mStart.setOnClickListener(this);
        mConnect.setOnClickListener(this);


        mAuto.setEnabled(false);
        mStop.setEnabled(false);

    }

    boolean isStart = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                isStart = true;
                play(i);
                mStart.setEnabled(false);
                mStop.setEnabled(true);
                mAuto.setEnabled(true);
                byte[] buffer = util.command((byte) 1, (byte) (i + 1));
                mBluetoothLeService.WriteValueBybyte(buffer);
                break;

            case R.id.stop:
                isStart = false;

                mediaPlayer.pause();
                mStop.setEnabled(false);
                mStart.setEnabled(true);
                mAuto.setEnabled(false);
                byte[] buffer1 = util.command((byte) 1, (byte) (4));
                mBluetoothLeService.WriteValueBybyte(buffer1);
                break;

            case R.id.auto:
                isAuto = !isAuto;
                if (isAuto) {
                    mAuto.setText("取消自动循环");
                    if (!isPlaying) {
                        play(i);
                    }
                } else {

                    mAuto.setText("开始自动循环");
                }
                break;

            case R.id.connect:

                if (!isConnect) {
                    mLoading.setImageResource(R.drawable.icon_loading);
                    Animation animation = new RotateAnimation(0, 340, mLoading.getWidth() / 2, mLoading.getHeight() / 2);
                    animation.setDuration(900);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.RESTART);
                    animation.start();
                    mLoading.startAnimation(animation);
                    mConnect.setText("正在连接");
                    mBluetoothLeService.connect(mDeviceAddress);
                } else {
                    mLoading.clearAnimation();
                    mLoading.setImageResource(R.drawable.icon_connect);
                    mConnect.setText("连接设备");
                    mBluetoothLeService.disconnect();

                }
                break;
            default:
                break;
        }

    }

    boolean isConnect = false;

    class DecAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDec.size();
        }

        @Override
        public Object getItem(int position) {
            return mDec.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            convertView = View.inflate(NewActivity.this, R.layout.list_item, null);
            TextView convertView1 = (TextView) convertView;
            if (position == i) {
                convertView.setBackgroundResource(R.drawable.btn2_pressed);
                convertView1.setTextColor(Color.BLACK);
            } else {
                convertView.setBackgroundResource(R.drawable.btn3_pressed);
                convertView1.setTextColor(Color.WHITE);


            }


            convertView1.setText(mDec.get(position));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isStart){
                        Toast.makeText(NewActivity.this, "请先启动系统", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i = position;
                    mDecAdapter.notifyDataSetChanged();
                    byte[] buffer =null;
                    if(position==2){
                        buffer=util.command((byte) 1, (byte) (position + 2));
                    }else {

                        buffer = util.command((byte) 1, (byte) (position + 1));
                    }
                    mBluetoothLeService.WriteValueBybyte(buffer);
                    play(position);

                }
            });
            return convertView;
        }
    }


    private void initFile() {
        File file = new File(getExternalFilesDir(MIDI_SERVICE).getAbsolutePath() + "/video");
        try {
            if (!file.exists()) {

                String filesDir = getExternalFilesDir(MIDI_SERVICE).getAbsolutePath() + "/abc";
                //从资源文件中复制视频
                if (!copyFileFromSD(filesDir)) {
                    copyFileFromAssert(filesDir);

                }

                unzip(filesDir, getExternalFilesDir(MIDI_SERVICE).getAbsolutePath());

            }

            file = new File(getExternalFilesDir(MIDI_SERVICE).getAbsolutePath() + "/video");
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                Log.e(TAG, "onCreate:没有视频文件 ");

                return;
            }
            for (File f : files) {
                if (f.getAbsolutePath().endsWith(".txt")) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                    String name = null;
                    while ((name = bufferedReader.readLine()) != null) {
                        mDec.add(name);
                        Log.e(TAG, "name= " + name);
                    }
                    continue;
                }
                mVideo.add(f.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean copyFileFromSD(String filesDir) throws IOException {

        Log.e(TAG, "copyFileFromSD: Environment.getExternalStorageState()=" + Environment.getExternalStorageState());
        Log.e(TAG, "copyFileFromSD: Environment.MEDIA_MOUNTED=" + Environment.MEDIA_MOUNTED);
        Log.e(TAG, "copyFileFromSD: Environment.MEDIA_MOUNTED=" + Environment.MEDIA_MOUNTED);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;

        }
        String path = Environment.getExternalStorageDirectory() + "/abc";
        Log.e(TAG, "copyFileFromSD:path=" + path);

        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(filesDir);
        int len = -1;
        byte[] buffur = new byte[1024];
        while ((len = inputStream.read(buffur)) != -1) {
            outputStream.write(buffur, 0, len);
            Log.e(TAG, "onCreatecopyFileFromSD:复制中 ");
        }
        outputStream.close();
        inputStream.close();
        Log.e(TAG, "onCreatecopyFileFromSD:复制完成 ");
        return true;
    }

    private void copyFileFromAssert(String filesDir) throws IOException {
        InputStream inputStream = getAssets().open("abc");
        FileOutputStream outputStream = new FileOutputStream(filesDir);
        int len = -1;
        byte[] buffur = new byte[1024];
        while ((len = inputStream.read(buffur)) != -1) {
            outputStream.write(buffur, 0, len);
            Log.e(TAG, "onCreatecopyFileFromAssert:复制中 ");
        }
        outputStream.close();
        inputStream.close();

        Log.e(TAG, "onCreatecopyFileFromAssert:复制完成 ");
    }


    public void setActionBarTitle(String title) {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);

// 显示自定义视图
        supportActionBar.setDisplayShowCustomEnabled(true);
//此处自定义了一个actionbar为了让标题居中显示
        actionbarLayout = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        supportActionBar.setCustomView(
                actionbarLayout,
                new ActionBar.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams mP = (ActionBar.LayoutParams) actionbarLayout
                .getLayoutParams();
        mP.gravity = mP.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK
                | Gravity.CENTER_HORIZONTAL;

        supportActionBar.setCustomView(actionbarLayout, mP);
        TextView titleText = (TextView) actionbarLayout.findViewById(R.id.anctionbar_title);
        titleText.setText(title);
    }

    private long unzip(String mInput, String mOutput) throws IOException {
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;

        zip = new ZipFile(mInput);
        entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            File destination = new File(mOutput, entry.getName());
            if (!destination.getParentFile().exists()) {
                Log.e(TAG, "make=" + destination.getParentFile().getAbsolutePath());
                destination.getParentFile().mkdirs();
            }
            ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
            extractedSize += copy(zip.getInputStream(entry), outStream);
            outStream.close();
        }

        return extractedSize;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }


    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
        }
    }


    @Override
    protected void onPause() {
        // 先判断是否正在播放
        if (mediaPlayer.isPlaying()) {
            // 如果正在播放我们就先保存这个播放位置
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        super.onPause();
    }

    boolean isAuto = false;
    boolean isPlaying = false;


    private void play(int position) {
        isPlaying = true;
        try {

            mSrfaceView.setBackground(null);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mVideo.get(position));
            mediaPlayer.prepare();


            // 播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //准备完成后播放
                    mediaPlayer.start();
                }
            });

            // Toast.makeText(this, "开始播放！", Toast.LENGTH_LONG).show();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    if (isAuto) {

                        if (i == mVideo.size() - 1) {
                            i = 0;
                        } else {

                            i++;
                        }

                        byte[] buffer = util.command((byte) 1, (byte) (i + 1));
                        mBluetoothLeService.WriteValueBybyte(buffer);
                        play(i);
                        mDecAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            isPlaying = false;
        }
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

    /**
     * 广播接收器 *
     */

    private void initBrocast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(mGattUpdateReceiver, intentFilter);
    }

    public BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 已连接
                mConnect.setText("连接成功");
                mLoading.setImageResource(R.drawable.icon_succes);
                mLoading.clearAnimation();
//                Log.e(TAG, "Only gatt, just wait");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) { // 断开连接
                mConnect.setText("连接断开");
                mLoading.setImageResource(R.drawable.icon_connect);
                mLoading.clearAnimation();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) // 发现服务
            {

                // btnSend.setEnabled(true);
//                Log.e(TAG, "In what we need");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // 接收数据
//                Log.e(TAG, "RECV DATA");
                byte[] inputBuffer = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String datas = util.byteToHexString(inputBuffer);

                Log.e("rec1", datas);
            }
        }
    };


}
