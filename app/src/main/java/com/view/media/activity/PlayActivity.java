package com.view.media.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.AppCompatSeekBar;
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
import com.view.media.model.DownLoadMusicModel;
import com.view.media.model.MusicLrcModel;
import com.view.media.model.MusicMvModel;
import com.view.media.utils.FileUtil;
import com.view.media.utils.TimeUtil;
import com.view.media.view.LyricView;

import java.io.File;
import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Destiny on 2016/12/19.
 */

public class PlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, NetWorkStateListener {
    private ImageView iv_start_pause, iv_prev, iv_next;
    private TextView tv_curr_duration, tv_total_duration;
    private AppCompatSeekBar sb_progress;
    private LyricView lrc_main;

    private File[] files;
    private int position;
    private ArrayList<SearchMusicBean> list;
    private PlayService.MyBinder binder;
    private PlayServiceVConnection conn = new PlayServiceVConnection();
    public DownLoadMusicApi downLoadMusicApi;
    private MusicLrcModel.Builder builder = new MusicLrcModel.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play);
        files = FileUtil.getFiles(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH);
        position = getIntent().getIntExtra("position", 0);
        list = (ArrayList<SearchMusicBean>) getIntent().getSerializableExtra("bean");
        initMedia();
        super.onCreate(savedInstanceState);
    }

    private void initMedia() {
        Intent intent = new Intent(this, PlayService.class);
        if (list == null) {
            intent.putExtra("file", files[position]);
        } else {
            intent.putExtra("bean", list.get(position));
        }
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    public void initData() {
        super.initData();

        iv_start_pause.setTag("0");

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
    }

    @Override
    public void setListener() {
        super.setListener();
        iv_start_pause.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        sb_progress.setOnSeekBarChangeListener(this);
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
        }

    }

    private void next() {
        lrc_main.refushLyric();
        if (list == null) {
            if (position == files.length - 1) {
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
                position = files.length;
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
            String name = files[position].getName();
            initToolBar(name,true);
            lrc_main.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + mToolbar.getTitle().toString().trim() + ".lrc");
        } else {
            initToolBar(list.get(position).name,true);

            downloadLrc();

            DownLoadMusicModel.Builder builder = new DownLoadMusicModel.Builder();
            DownLoadMusicModel model = builder.setUrl(list.get(position).mp3url)
                    .setSingerName(list.get(position).singerName)
                    .setSongName(list.get(position).name)
                    .setAlbumName(list.get(position).albumName)
                    .create();

            downLoadMusicApi = new DownLoadMusicApi(model);

            MusicMvModel mvModel=new MusicMvModel(list.get(position).mvId);
            MusicMvApi mvApi=new MusicMvApi(mvModel,null,this);
            mvApi.getMv(false);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }

}
