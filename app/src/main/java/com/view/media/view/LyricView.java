package com.view.media.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.view.media.utils.DensityUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Destiny on 2016/12/22.
 */

public class LyricView extends View {
    private ArrayList<LyricModel> lrcs = new ArrayList<LyricModel>();
    private int currentPosition = 0;
    private float mAnimateOffset;
    private ValueAnimator mAnimator;

    private final String STR_NOTHING="暂无歌词";

    public LyricView(Context context) {
        super(context);
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 外部提供方法
    // 设置lrc的路径
    public void setLrcPath(String path) {
        lrcs.clear();

        File file = new File(path);
        if (!file.exists()) {
            postInvalidate();
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while (null != (line = reader.readLine())) {
                if (parseLine(line) != null) {
                    lrcs.add(parseLine(line));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        postInvalidate();
    }

    private LyricModel parseLine(String line) {

        Matcher matcher = Pattern.compile("\\[\\d.+\\].+").matcher(line);
        // 如果形如：[xxx]后面啥也没有的，则return空
        if (!matcher.matches()) {
            return null;
        }

        LyricModel model = new LyricModel();

        String time = line.substring(1, line.indexOf("]"));
        String minutes = time.split("\\:")[0];
        String second = time.split("\\:")[1].split("\\.")[0];
        String msec = time.split("\\:")[1].split("\\.")[1];
        long ms = Integer.parseInt(minutes) * 60000 + Integer.parseInt(second) * 1000 + Integer.parseInt(msec);
        model.time = ms;
        model.lrc = line.substring(line.indexOf("]") + 1);

        return model;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /***********初始化画笔***********/
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(DensityUtil.dip2px(getContext(),20));
        mTextPaint.setColor(Color.BLUE);
        /***********初始化画笔***********/

        if (lrcs==null||lrcs.size()==0){
            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(STR_NOTHING, getWidth() / 2 - mTextPaint.measureText(STR_NOTHING) / 2, getHeight() / 2, mTextPaint);
            return;
        }

        canvas.translate(0, mAnimateOffset);

        /***********画当前行***********/
        canvas.drawText(lrcs.get(currentPosition).lrc, getWidth() / 2 - mTextPaint.measureText(lrcs.get(currentPosition).lrc) / 2, getHeight() / 2, mTextPaint);
        lrcs.get(currentPosition).pointY = getHeight() / 2;
        /***********画当前行***********/

        /***********画当前行之前***********/
        if (currentPosition > 0) {
            for (int position = currentPosition - 1, i = 1; position >= 0; position--, i++) {
                /***********如果所画行已超出屏幕，则退出循环不在绘画***********/
                if (mAnimator == null || !mAnimator.isRunning()) {
                    if (getHeight() / 2 - i * 40 < 0) {
                        break;//退出
                    }
                }

                mTextPaint.setColor(Color.WHITE);
                canvas.drawText(lrcs.get(position).lrc, getWidth() / 2 - mTextPaint.measureText(lrcs.get(position).lrc) / 2, getHeight() / 2 - i * 60, mTextPaint);
                lrcs.get(position).pointY = getHeight() / 2 - i * 60;
            }
        }
        /***********画当前行之前***********/

        /***********画当前行之后***********/
        for (int position = currentPosition, i = 0; position < lrcs.size(); position++, i++) {
            /***********如果所画行已超出屏幕，则退出循环不在绘画***********/
            if (mAnimator == null || !mAnimator.isRunning()) {
                if (getHeight() / 2 + i * 40 > getHeight()) {
                    break;//退出
                }
            }
            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(lrcs.get(position).lrc, getWidth() / 2 - mTextPaint.measureText(lrcs.get(position).lrc) / 2, getHeight() / 2 + i * 60, mTextPaint);
            lrcs.get(position).pointY = getHeight() / 2 + i * 60;
        }
        /***********画当前行之后***********/


    }

    public void refushLyric(){
        currentPosition=0;
    }

    public synchronized void changeCurrent(long time) {

        if (lrcs==null||lrcs.size()==0){
            return;
        }

        if (currentPosition + 1 == lrcs.size()) {
            return;
        }

        if (lrcs.get(currentPosition + 1).time <= time) {
            currentPosition++;
            lrcs.get(currentPosition).isCurrent = true;
            lrcs.get(currentPosition - 1).isCurrent = false;
            newlineOnUI();
        }
    }

    public void onDrag(int progress) {

        if (lrcs==null||lrcs.size()==0){
            return;
        }

        if (lrcs.get(lrcs.size()-1).time < progress){
            lrcs.get(currentPosition).isCurrent = false;
            currentPosition=lrcs.size()-1;
            lrcs.get(currentPosition).isCurrent = true;
            newlineOnUI();
            return;
        }

        for (int i = 0; i < lrcs.size(); i++) {
            lrcs.get(i).isCurrent = false;
            if (lrcs.get(i).time > progress) {
                currentPosition = i - 1;
                if (currentPosition==-1){
                    currentPosition=0;
                }
                lrcs.get(currentPosition).isCurrent = true;
                newlineOnUI();
                return;
            }
        }
    }

    /**
     * 换行动画<br>
     * 属性动画只能在主线程使用
     */
    private void newlineAnimation() {
        stopAnimation();
        mAnimator = ValueAnimator.ofFloat(DensityUtil.dip2px(getContext(),20), 0);
        mAnimator.setDuration(500);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimateOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    private void newlineOnUI() {
        post(new Runnable() {
            @Override
            public void run() {
                newlineAnimation();
            }
        });
    }

    public class LyricModel {
        public String lrc;
        public long time;
        public int pointY;
        public boolean isCurrent;
    }


}
