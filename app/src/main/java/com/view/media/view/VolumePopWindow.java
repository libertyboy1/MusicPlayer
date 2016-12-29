package com.view.media.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.view.media.R;


public class VolumePopWindow extends PopupWindow {
    private View mMenuView;
    private VerticalSeekBar seekBar;


    public VolumePopWindow(Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.volume, null);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        initView(context);

    }

    private void initView(Activity context) {
        final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar = (VerticalSeekBar) mMenuView.findViewById(R.id.seekBar);
        seekBar.setMax(max);
        seekBar.setProgress(current);
        seekBar.onProgressRefresh(current,true);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

}
