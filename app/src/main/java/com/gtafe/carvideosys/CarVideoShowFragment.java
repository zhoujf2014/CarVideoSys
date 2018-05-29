package com.gtafe.carvideosys;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;


/**
 * Created by jie.gao2 on 2016/10/17.
 */
public class CarVideoShowFragment extends Fragment implements View.OnClickListener{
    private View view;
    private VideoView videoView;
    public static Button auto;
    public int[] video_arr=new int[]{1};
    int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.carvideo_show,container,false);
        videoView=(VideoView) view.findViewById(R.id.videoshow);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoPlay(0);
    }

    public void videoPlay(int no){

        String uri = "android.resource://" + getActivity().getPackageName() + "/" + video_arr[no];
        videoView.setVideoURI(Uri.parse(uri));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        videoView.start();
    }

    public boolean isPlaying(){
        boolean isPlaying=videoView.isPlaying();
        return isPlaying;
    }

    //视频停止
    public void videoStop(){
        videoView.stopPlayback();
    }

    //视频暂停
    public void videoPause(){
        videoView.pause();
        position=videoView.getCurrentPosition();
    }

    //视频继续播放
    public void videoResume(){
        videoView.seekTo(position);
        videoView.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.auto:
                /*if(isAuto == 0){
                    TimerTask task = new TimerTask(){
                        public void run() {
                            if(time> (video_arr.length-1)){
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
                    timer.schedule(task,0,5000);
                    isAuto=1;
                    auto.setText("取消循环");
                    auto.setBackgroundColor(Color.RED);
                    break;
                }else if(isAuto == 1){
                    timer.cancel();
                    auto.setText("自动循环");
                    auto.setBackgroundColor(Color.GREEN);
                    isAuto=0;
                }*/
        }
    }
}
