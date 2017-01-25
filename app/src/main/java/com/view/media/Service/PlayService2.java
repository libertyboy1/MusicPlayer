package com.view.media.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.view.media.bean.SearchMusicBean;
import com.view.media.db.TableMusic;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Destiny on 2016/12/19.
 */

public class PlayService2 extends Service {
    private MediaPlayer mp;
    private IBinder binder = new MyBinder();
    public OnMusicListener myListener;

    public interface OnMusicListener {

        void nextMusic();

        void prevMusic();
    }


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
            return mp!=null?mp.getDuration():0;
        }

        public void setOnMusicListener(OnMusicListener listener) {
            myListener = listener;
        }

        public void setDuration(int duration) {
            mp.seekTo(duration);
        }

        public void setDataSource(String url) {
            mp = new MediaPlayer();
            try {
                mp.setDataSource(url);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (myListener != null) {
                            myListener.nextMusic();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public MediaPlayer getMediaPlayer(){
            return mp;
        }

        public void prepare(){
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    private void stopMedia() {
        if (mp!=null){
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
