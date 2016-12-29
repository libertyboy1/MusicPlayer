package com.view.media.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.Service.PlayService;
import com.view.media.api.DownLoadMusicApi;
import com.view.media.api.MusicLrcApi;
import com.view.media.api.MusicMvApi;
import com.view.media.api.NetWorkStateListener;
import com.view.media.bean.SearchMusicBean;
import com.view.media.constant.Constant;
import com.view.media.db.TableMusic;
import com.view.media.model.DownLoadMusicModel;
import com.view.media.model.MusicLrcModel;
import com.view.media.model.MusicMvModel;
import com.view.media.utils.FileUtil;
import com.view.media.utils.StringUtil;
import com.view.media.utils.TimeUtil;
import com.view.media.view.LyricView;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.name;
import static android.os.Build.VERSION_CODES.M;

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
    private PlayService.MyBinder binder;
    private PlayServiceVConnection conn = new PlayServiceVConnection();
    public DownLoadMusicApi downLoadMusicApi;
    private MusicLrcModel.Builder builder = new MusicLrcModel.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play);

        position = getIntent().getIntExtra("position", 0);
        list = (ArrayList<SearchMusicBean>) getIntent().getSerializableExtra("bean");//网络列表
        tb_musics = (ArrayList<TableMusic>) getIntent().getSerializableExtra("tb_musics");//本地列表

        initMedia();
        super.onCreate(savedInstanceState);
    }

    private void initMedia() {
        Intent intent = new Intent(this, PlayService.class);
        if (list == null) {
            intent.putExtra("tb_music", tb_musics.get(position));
        } else {
            intent.putExtra("bean", list.get(position));
        }
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    public void initData() {
        super.initData();

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
                    binder.pause();
                } else {
                    iv_start_pause.setTag("0");
                    iv_start_pause.setImageResource(R.drawable.landscape_pause_btn);
                    binder.start();
                }
                break;
            case R.id.iv_prev:
                prev();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_mv:
                binder.pause();
                Intent intent = new Intent(this, MvActivity.class);
                intent.putExtra("soundName", mToolbar.getTitle().toString().trim());
                if (list == null) {
                    intent.putExtra("mvid", tb_musics.get(position).getMvId());
                }else{
                    intent.putExtra("mvid", list.get(position).mvId);
                }
                startActivityForResult(intent, 1);
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (binder != null) {
            binder.start();
        }

    }

    private void next() {
        lrc_main.refushLyric();
        if (list == null) {
            if (position == tb_musics.size() - 1) {
                position = -1;
            }
        } else {
            if (position == list.size() - 1) {
                position = -1;
            }
        }

        position++;
        unBindMusicService();
        initMedia();
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
        unBindMusicService();
        initMedia();
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
        binder.setDuration(duration);
        lrc_main.onDrag(duration);
    }

    @Override
    public void onSuccess() {
        lrc_main.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");
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

    public class PlayServiceVConnection implements ServiceConnection, PlayService.OnMusicListener {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (PlayService.MyBinder) iBinder;
            tv_total_duration.setText(TimeUtil.getStrTimeFromeTimestamp(binder.getDuration()));
            binder.setCurDurationListener(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }

        @Override
        public void curDuration(long duration) {
            lrc_main.changeCurrent(duration);
            tv_curr_duration.setText(TimeUtil.getStrTimeFromeTimestamp(duration));
            sb_progress.setProgress((int) ((duration * 1f / binder.getDuration()) * 100));
        }

        @Override
        public void nextMusic() {
            next();
        }

        @Override
        public void prevMusic() {
            prev();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindMusicService();
    }

    private void unBindMusicService() {
        binder.stop();
        unbindService(conn);
    }

    private void setTitle() {

        if (list == null) {
            initToolBar(tb_musics.get(position).getSoungName(), true);
            lrc_main.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");


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

}
