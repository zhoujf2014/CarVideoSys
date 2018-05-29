package com.gtafe.carvideosys;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jie.gao2 on 2016/10/20.
 */
public class CarVideoActivity extends BaseActivity implements View.OnClickListener{
    public  List<String> list=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private String projectName;
    private Button start;
    private Button stop;
    private Button auto;
    private CarVideoShowFragment carVideoShow;

    private View cache= CarVideoListFragment.view;
    private Timer timer=new Timer(true);
    private int isAuto=0;
    private int time=0;

    @Override
    protected void getInfo(byte[] inputBuffer) {

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int time = Integer.parseInt(String.valueOf(msg.obj));
//            videoPlay(CarVideoFragment.list.get(time),time);
                byte[] buffer = util.command((byte) 1, (byte) (time + 1));
                mBluetoothLeService.WriteValueBybyte(buffer);
            carVideoShow.videoPlay(time);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carvideo_fragment);
        start=(Button) findViewById(R.id.start);
        stop=(Button) findViewById(R.id.stop);
        auto=(Button) findViewById(R.id.auto);
        FragmentManager fm = getFragmentManager();
        carVideoShow = (CarVideoShowFragment) fm.findFragmentById(R.id.fragment_show);


        ActionBar actionBar=getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        listView=(ListView) findViewById(R.id.videolist);
//      list = new ArrayList<String>();
        File path = new File("data/data/com.example.jiegao2.carvideosys");
        path.mkdir();
        try {
            AssetManager assetmanager = getApplicationContext().getAssets();
            InputStream inputstream = assetmanager.open("database.db");

            FileOutputStream fileoutputstream = new FileOutputStream("/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + "com.example.jiegao2.carvideosys" + "/" + "database.db");
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputstream.read(buffer)) > 0) {
                fileoutputstream.write(buffer, 0, count);
            }
            fileoutputstream.flush();
            fileoutputstream.close();
            inputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String DB_PATH = "/data/data/com.example.jiegao2.carvideosys/database.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);
        Cursor platform_cursor = db.rawQuery("select * from platform", null);
        while (platform_cursor.moveToNext()) {
            projectName = platform_cursor.getString(platform_cursor.getColumnIndex("name"));
        }
        actionBar.setTitle(projectName);
        Cursor option_cursor = db.rawQuery("select * from optionlist", null);
        int no=1;
        while (option_cursor.moveToNext()) {
            String modulename =option_cursor.getString(option_cursor.getColumnIndex("name"));
            list.add(modulename);
            no++;
        }
        /*list=new OperationService(getActivity().getApplicationContext(),"database.db").queryShowName();*/
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(null);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        auto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if(cache != null){
                                cache.setBackgroundColor(0xFFFFA042);
                            }
                            view.setBackgroundColor(Color.WHITE);
                            timer.cancel();
                            auto.setText("自动循环");
                            auto.setBackgroundResource(R.drawable.entry_btn_selector);
                            isAuto=0;
//                          carVideoShow.videoPlay(list.get(i),i);
                            byte[] buffer = util.command((byte) 1, (byte) (i + 1));
                            mBluetoothLeService.WriteValueBybyte(buffer);
                            carVideoShow.videoPlay(i);
                            cache=view;
                        }
                    });
                    byte[] buffer=util.command((byte)1,(byte)0);
                    mBluetoothLeService.WriteValueBybyte(buffer);
                    start.setEnabled(false);
                    stop.setEnabled(true);
                    auto.setEnabled(true);

                break;
            case R.id.stop:

                byte[] buffer1=util.command((byte)0,(byte)0);
                mBluetoothLeService.WriteValueBybyte(buffer1);
                listView.setOnItemClickListener(null);
                timer.cancel();
                start.setEnabled(true);
                stop.setEnabled(false);
                auto.setText("自动循环");
                auto.setBackgroundResource(R.drawable.entry_btn_selector);
                auto.setEnabled(false);
                if(cache != null){
                    cache.setBackgroundColor(0xFFFFA042);
                }
                carVideoShow.videoStop();
                break;
            case R.id.auto:
                if(cache != null){
                    cache.setBackgroundColor(0xFFFFA042);
                }
                if(isAuto == 0){
                    TimerTask task = new TimerTask(){
                        public void run() {
                            if(time> (carVideoShow.video_arr.length-1)){
                                time=0;
                            }
                            Message message=handler.obtainMessage();
                            message.obj=time;
                            handler.sendMessage(message);
                            time++;
                            Log.i("time",time+"");
                        }
                    };
                    timer=new Timer(true);
                    timer.schedule(task,0,20000);          //设置视频自动轮播每个视频相隔播放时间
                    isAuto=1;
                    auto.setText("取消循环");
                    auto.setBackgroundResource(R.drawable.entry_btn_pressed_selector);
                    break;
                }else if(isAuto == 1){
                    timer.cancel();
                    auto.setText("自动循环");
                    auto.setBackgroundResource(R.drawable.entry_btn_selector);
                    isAuto=0;
                }
        }
    }
}
