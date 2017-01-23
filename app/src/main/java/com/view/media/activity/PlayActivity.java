package com.view.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.api.DownLoadMusicApi;
import com.view.media.api.MusicLrcApi;
import com.view.media.api.NetWorkStateListener;
import com.view.media.bean.SearchMusicBean;
import com.view.media.constant.Constant;
import com.view.media.db.TableMusic;
import com.view.media.apiModel.DownLoadMusicModel;
import com.view.media.apiModel.MusicLrcModel;
import com.view.media.utils.StringUtil;
import com.view.media.utils.TimeUtil;
import com.view.media.view.LyricView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.view.media.activity.MainActivity.binder;

/**
 * Created by Destiny on 2016/12/19.
 */

public class PlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, NetWorkStateListener {
    private ImageView iv_start_pause, iv_prev, iv_next, iv_mv;
    private TextView tv_curr_duration, tv_total_duration;
    private AppCompatSeekBar sb_progress;
    private LyricView lrc_main;

    private ArrayList<TableMusic> tb_musics;
    private int position;
    private ArrayList<SearchMusicBean> list;
    public DownLoadMusicApi downLoadMusicApi;
    private MusicLrcModel.Builder builder = new MusicLrcModel.Builder();

    private Timer timer = new Timer();

    private Handler cur_duration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lrc_main.changeCurrent(MainActivity.binder.getMediaPlayer().getCurrentPosition());
            tv_curr_duration.setText(TimeUtil.getStrTimeFromeTimestamp((long) MainActivity.binder.getMediaPlayer().getCurrentPosition()));
            sb_progress.setProgress((int) ((MainActivity.binder.getMediaPlayer().getCurrentPosition() * 1f / MainActivity.binder.getDuration()) * 100));
        }
    };

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            cur_duration.sendEmptyMessage(1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play);

        position = getIntent().getIntExtra("position", 0);

        list = (ArrayList<SearchMusicBean>) getIntent().getSerializableExtra("bean");//网络列表
        tb_musics = (ArrayList<TableMusic>) getIntent().getSerializableExtra("tb_musics");//本地列表

        timer.schedule(timerTask, 0, 1000);

        super.onCreate(savedInstanceState);
    }


    @Override
    public void initData() {
        super.initData();

        tv_total_duration.setText(TimeUtil.getStrTimeFromeTimestamp((long) MainActivity.binder.getDuration()));

        iv_start_pause.setTag("0");

        if (list != null) {
            if (StringUtil.isEmpty(list.get(position).mvId)) {
                iv_mv.setVisibility(View.GONE);
            } else {
                iv_mv.setVisibility(View.VISIBLE);
            }
        } else {
            if (StringUtil.isEmpty(tb_musics.get(position).getMvId())) {
                iv_mv.setVisibility(View.GONE);
            } else {
                iv_mv.setVisibility(View.VISIBLE);
            }
        }

        setTitle();
    }

    private void downloadLrc() {
        if (list != null) {
            MusicLrcModel model = builder.setId(list.get(position).id).setName(list.get(position).name).create();
            MusicLrcApi api = new MusicLrcApi(model, this, this);
            api.musicLrcApi();
        }
    }

    @Override
    public void initView() {
        super.initView();
        iv_start_pause = (ImageView) findViewById(R.id.iv_start_pause);
        iv_prev = (ImageView) findViewById(R.id.iv_prev);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tv_curr_duration = (TextView) findViewById(R.id.tv_curr_duration);
        tv_total_duration = (TextView) findViewById(R.id.tv_total_duration);
        sb_progress = (AppCompatSeekBar) findViewById(R.id.sb_progress);
        lrc_main = (LyricView) findViewById(R.id.lrc_main);
        iv_mv = (ImageView) findViewById(R.id.iv_mv);
    }

    @Override
    public void setListener() {
        super.setListener();
        iv_start_pause.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        sb_progress.setOnSeekBarChangeListener(this);
        iv_mv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.iv_start_pause:
                if (iv_start_pause.getTag().equals("0")) {
                    iv_start_pause.setTag("1");
                    iv_start_pause.setImageResource(R.drawable.landscape_play_btn);
                    MainActivity.binder.pause();
                } else {
                    iv_start_pause.setTag("0");
                    iv_start_pause.setImageResource(R.drawable.landscape_pause_btn);
                    MainActivity.binder.start();
                }
                break;
            case R.id.iv_prev:
                prev();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_mv:
                MainActivity.binder.pause();
                Intent intent = new Intent(this, MvActivity.class);
                intent.putExtra("soundName", mToolbar.getTitle().toString().trim());
                if (list == null) {
                    intent.putExtra("mvid", tb_musics.get(position).getMvId());
                } else {
                    intent.putExtra("mvid", list.get(position).mvId);
                }
                startActivityForResult(intent, 1);
                break;
        }

    }


    private void next() {
        lrc_main.refushLyric();
        if (list == null) {
            if (position == tb_musics.size() - 1) {
                position = -1;
            }
            position++;
            MainActivity.binder.stop();
            MainActivity.binder.setDataSource(tb_musics.get(position).getMp3Url());
            MainActivity.binder.start();
        } else {
            if (position == list.size() - 1) {
                position = -1;
            }
            position++;
            MainActivity.binder.stop();
            MainActivity.binder.setDataSource(list.get(position).mp3url);
            MainActivity.binder.start();
        }


        setTitle();
    }

    ;

    private void prev() {
        lrc_main.refushLyric();
        if (position == 0) {
            if (list == null) {
                position = tb_musics.size();
            } else {
                position = list.size();
            }
        }
        position--;
        MainActivity.binder.stop();
        if (list == null) {
            MainActivity.binder.setDataSource(tb_musics.get(position).getMp3Url());
        } else {
            MainActivity.binder.setDataSource(list.get(position).mp3url);
        }

        MainActivity.binder.start();
        setTitle();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int duration = (int) (seekBar.getProgress() / 100.0 * binder.getDuration());
        Log.e("PlayActivity", "duration:" + duration);
        binder.setDuration(duration);
        lrc_main.onDrag(duration);
    }

    @Override
    public void onSuccess() {
        Log.e("1---", Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");
        lrc_main.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");
        lrc_main.onDrag(MainActivity.binder.getMediaPlayer().getCurrentPosition());
    }


    @Override
    public void onError() {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onFinished() {

    }

    private void setTitle() {

        if (list == null) {
            initToolBar(tb_musics.get(position).getSoungName(), true);
            lrc_main.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");
            lrc_main.onDrag(MainActivity.binder.getMediaPlayer().getCurrentPosition());
        } else {
            initToolBar(list.get(position).name, true);

            downloadLrc();

            DownLoadMusicModel.Builder builder = new DownLoadMusicModel.Builder();
            DownLoadMusicModel model = builder.setUrl(list.get(position).mp3url)
                    .setSingerName(list.get(position).singerName)
                    .setSongName(list.get(position).name)
                    .setAlbumName(list.get(position).albumName)
                    .setMid(list.get(position).id)
                    .setMvid(list.get(position).mvId)
                    .create();

            downLoadMusicApi = new DownLoadMusicApi(model);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);

        if (list == null) {
            menu.getItem(1).setVisible(false);
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.cancel();
        timerTask = null;
        timer = null;
    }
}
