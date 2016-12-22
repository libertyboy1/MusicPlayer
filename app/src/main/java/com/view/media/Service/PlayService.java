package com.view.media.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.LoginFilter;
import android.util.Log;

import com.view.media.activity.PlayActivity;
import com.view.media.bean.SearchMusicBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Destiny on 2016/12/19.
 */

public class PlayService extends Service {
    private MediaPlayer mp = new MediaPlayer();
    private IBinder binder = new MyBinder();
    private Timer timer = new Timer();
    public OnMusicListener myListener;

    public interface OnMusicListener {
        void curDuration(long duration);

        void nextMusic();

        void prevMusic();
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            cur_duration.sendEmptyMessage(1);
        }
    };

    private Handler cur_duration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (myListener != null) {
                myListener.curDuration(mp.getCurrentPosition());
            }
        }
    };

    public class MyBinder extends Binder {

        public void start() {
            mp.start();
        }

        public void pause() {
            mp.pause();
        }

        public void stop() {
            stopMedia();
        }

        public long getDuration() {
            return mp.getDuration();
        }

        public void setCurDurationListener(OnMusicListener listener) {
            myListener = listener;
        }

        public void setDuration(int duration) {
            mp.seekTo(duration);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        File file = (File) intent.getSerializableExtra("file");
        SearchMusicBean musicBean = (SearchMusicBean) intent.getSerializableExtra("bean");
        try {
            if (musicBean == null) {
                mp.setDataSource(file.getAbsolutePath());
            } else {
                mp.setDataSource(musicBean.mp3url);
            }

            mp.prepare();
            mp.start();
            timer.schedule(timerTask, 0, 1000);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (myListener != null) {
                        myListener.nextMusic();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binder;
    }


    private void stopMedia() {
        timerTask.cancel();
        mp.stop();
        mp.release();
        mp = null;
    }
}
