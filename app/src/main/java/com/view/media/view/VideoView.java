package com.view.media.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.view.media.R;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Destiny on 2016/12/28.
 */

public class VideoView extends FrameLayout implements View.OnClickListener,MediaPlayer.OnPreparedListener,Animator.AnimatorListener{
    private android.widget.VideoView vv_main;
    private Toolbar mToolbar;
    private TextView tv_total_duration;
    private TextView tv_curr_duration;
    private AppCompatSeekBar sb_progress;
    private ImageView iv_start_pause;
    private LinearLayout ll_controller;
    private AppBarLayout abl_titlebar;

    private final int CURRENT_POSITION_ID=1;
    private final int SHOW_GONG_ID=2;
    private final long ONEHOURMS = 3600000;//一小时的毫秒数

    private boolean isAnimatorStop=true;
    private boolean isShow=true;

    private Timer timer = new Timer();
    private SimpleDateFormat formatter_hour = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat formatter_no_hour = new SimpleDateFormat("mm:ss");

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            seeTime.sendEmptyMessage(CURRENT_POSITION_ID);
        }
    };

    private TimerTask timerTask1 = new TimerTask() {
        @Override
        public void run() {
            seeTime.sendEmptyMessage(SHOW_GONG_ID);
        }
    };

    private Handler seeTime = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CURRENT_POSITION_ID:
                    if (vv_main.isPlaying()){
                        sb_progress.setProgress((int) ((double) vv_main.getCurrentPosition() / vv_main.getDuration() * 100));
                        if (vv_main.getDuration() < ONEHOURMS) {
                            tv_curr_duration.setText(formatter_no_hour.format(vv_main.getCurrentPosition()));
                        } else {
                            tv_curr_duration.setText(formatter_hour.format(vv_main.getCurrentPosition()));
                        }
                    }
                    break;
                case SHOW_GONG_ID:
                    Log.e("-----","handler");
                    if (isShow){
                        gongControllerAndTitle();
                    }
                    break;
            }
        }
    };

    private void gongControllerAndTitle(){
        if (isAnimatorStop){
            float curY_bottom = ll_controller.getY();
            ObjectAnimator bottom_animator = ObjectAnimator.ofFloat(ll_controller, "y",
                    curY_bottom, curY_bottom + ll_controller.getHeight());
            bottom_animator.setDuration(200);
            bottom_animator.start();
            bottom_animator.addListener(this);

            float curY_top = abl_titlebar.getY();
            ObjectAnimator top_animator = ObjectAnimator.ofFloat(abl_titlebar, "y",
                    curY_top, curY_top - abl_titlebar.getHeight());
            top_animator.setDuration(200);
            top_animator.start();

            isShow=false;
        }
    }

    private void showControllerAndTitle(){
        if (isAnimatorStop){
            float curY_bottom = ll_controller.getY();
            ObjectAnimator bottom_animator = ObjectAnimator.ofFloat(ll_controller, "y",
                    curY_bottom, curY_bottom - ll_controller.getHeight());
            bottom_animator.setDuration(200);
            bottom_animator.start();
            bottom_animator.addListener(this);

            float curY_top = abl_titlebar.getY();
            ObjectAnimator top_animator = ObjectAnimator.ofFloat(abl_titlebar, "y",
                    curY_top, curY_top + abl_titlebar.getHeight());
            top_animator.setDuration(200);
            top_animator.start();

            isShow=true;

        }
    }

    public VideoView(Context context) {
        super(context);
        initView();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.video, null);
        vv_main= (android.widget.VideoView) view.findViewById(R.id.vv_main);
        mToolbar= (Toolbar) view.findViewById(R.id.tb_main);
        mToolbar.setNavigationIcon(R.mipmap.back);
        tv_total_duration= (TextView) view.findViewById(R.id.tv_total_duration);
        tv_curr_duration= (TextView) view.findViewById(R.id.tv_curr_duration);
        sb_progress= (AppCompatSeekBar) view.findViewById(R.id.sb_progress);
        iv_start_pause= (ImageView) view.findViewById(R.id.iv_start_pause);
        ll_controller= (LinearLayout) view.findViewById(R.id.ll_controller);
        abl_titlebar= (AppBarLayout) view.findViewById(R.id.abl_titlebar);

        iv_start_pause.setTag(1);//暂停0，开始1

        mToolbar.setBackgroundColor(Color.parseColor("#40000000"));

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity= Gravity.CENTER;
        vv_main.setLayoutParams(params);

        setListener();

        addView(view);
    }

    private void setListener(){

        vv_main.setOnPreparedListener(this);
        iv_start_pause.setOnClickListener(this);
        vv_main.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.e("---onTouch----","抬起");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.e("---onTouch----","按下");
                        if (!isShow){
                            showControllerAndTitle();
//                            timer.schedule(timerTask1,1, 4000);
                        }else{
                            gongControllerAndTitle();
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("---onTouch----","移动");
                        break;
                }

                return false;
            }
        });

        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo((int) (vv_main.getDuration() * (seekBar.getProgress() / 100.0)));
            }
        });

        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)getContext()).finish();
            }
        });

    }

    public void setVideoUri(Uri uri){
        vv_main.setVideoURI(uri);
    }

    public void setVideoPath(String path){
        vv_main.setVideoPath(path);
    }

    public void start(){
        vv_main.start();
        iv_start_pause.setTag(1);
        iv_start_pause.setImageResource(R.mipmap.mv_pause);
    }

    public void pause(){
        vv_main.pause();
        iv_start_pause.setTag(0);
        iv_start_pause.setImageResource(R.mipmap.mv_start);
    }

    public void seekTo(int msec){
        vv_main.seekTo(msec);
        start();
    }

    public int getDuration(){
        return  vv_main.getDuration();
    }

    public void setTitle(String title){
        mToolbar.setTitle(title);
        mToolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_start_pause:
                if ((int)iv_start_pause.getTag()==0){
                    start();
                }else{
                    pause();
                }
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (vv_main.getDuration() < ONEHOURMS) {
            tv_total_duration.setText(formatter_no_hour.format(vv_main.getDuration()));
        } else {
            tv_total_duration.setText(formatter_hour.format(vv_main.getDuration()));
        }
        timer.schedule(timerTask,0, 1000);
//        timer.schedule(timerTask1,1, 4000);
    }

    @Override
    public void onAnimationStart(Animator animator) {
        isAnimatorStop=false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        isAnimatorStop=true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
