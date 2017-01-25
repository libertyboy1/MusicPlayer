package com.view.media.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.Service.PlayService;
import com.view.media.Service.PlayService2;
import com.view.media.adapter.MusicListAdapter;
import com.view.media.api.DownLoadFinishInterface;
import com.view.media.api.DownLoadMusicApi;
import com.view.media.bean.DownLoadBean;
import com.view.media.bean.SearchMusicList;
import com.view.media.db.DbManage;
import com.view.media.db.TableMusic;
import com.view.media.utils.TimeUtil;
import com.view.media.view.ListSlideView;

import org.xutils.ex.DbException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Looper.prepare;
import static com.view.media.R.id.lrc_main;
import static com.view.media.R.id.sb_progress;
import static com.view.media.R.id.tv_curr_duration;
import static com.view.media.R.id.tv_total_duration;
import static com.view.media.bean.DownLoadBean.position;


public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, DownLoadFinishInterface {
    private ListSlideView lv_main;
    private ImageView iv_nothing;
    private LinearLayout ll_download;
    private TextView tv_download_num;
    public static TextView tv_sound_name, tv_singer_name;
    private AppCompatSeekBar sb_progress;
    private ImageView iv_start_pause;

    private MusicListAdapter adapter;
    private ArrayList<TableMusic> tb_musics;

    private Timer timer = new Timer();

    private int currentPosition = -1;

    public static PlayService2.MyBinder binder;
    private PlayServiceVConnection conn = new PlayServiceVConnection();

    public static MusicListener musicListener;

    public interface MusicListener{
        void nextMusic(int position);
        void prevMusic(int position);
    }

    private Handler cur_duration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (binder.getMediaPlayer() != null) {
                sb_progress.setProgress((int) ((binder.getMediaPlayer().getCurrentPosition() * 1f / binder.getDuration()) * 100));
            }
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
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, PlayService2.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        super.initData();

        try {
            tb_musics = (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (tb_musics == null || tb_musics.size() == 0) {
            iv_nothing.setVisibility(View.VISIBLE);
        } else {
            iv_nothing.setVisibility(View.GONE);
        }

        tv_download_num.setText("(" + DownLoadBean.downloadApis.size() + ")");

        adapter = new MusicListAdapter(this, tb_musics);
        lv_main.setAdapter(adapter);

        initToolBar("", false);
        mToolbar.setLogo(R.mipmap.icon);

    }

    @Override
    public void initView() {
        super.initView();
        lv_main = (ListSlideView) findViewById(R.id.lv_main);
        iv_nothing = (ImageView) findViewById(R.id.iv_nothing);
        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        tv_download_num = (TextView) findViewById(R.id.tv_download_num);
        tv_sound_name = (TextView) findViewById(R.id.tv_sound_name);
        tv_singer_name = (TextView) findViewById(R.id.tv_singer_name);
        sb_progress = (AppCompatSeekBar) findViewById(R.id.sb_progress);
        iv_start_pause = (ImageView) findViewById(R.id.iv_start_pause);
        iv_start_pause.setTag("0");
    }

    @Override
    public void setListener() {
        super.setListener();
        lv_main.setOnItemClickListener(this);
        ll_download.setOnClickListener(this);
        iv_start_pause.setOnClickListener(this);

        DownLoadMusicApi.downLoadFinishInterface = this;

        adapter.setRemoveListener(new MusicListAdapter.OnRemoveListener() {

            @Override
            public void onRemoveItem(int position) {
                try {
                    DbManage.manager.delete(tb_musics.get(position));
                    new File(tb_musics.get(position).getFilePath()).delete();
                    tb_musics = (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
                    adapter = new MusicListAdapter(MainActivity.this, tb_musics);
                    lv_main.setAdapter(adapter);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        SearchMusicList.temp_beans=null;
        if (currentPosition != i) {
            startPlayMusic(i);

            binder.getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("MainActivity", "开始播放");
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    intent.putExtra("position", i);
                    intent.putExtra("tb_musics", tb_musics);
                    tv_sound_name.setText(tb_musics.get(i).getSoungName());
                    tv_singer_name.setText(tb_musics.get(i).getSingerName());
                    startActivity(intent);
                    currentPosition = i;
                }
            });

        } else {
            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
            intent.putExtra("position", i);
            intent.putExtra("tb_musics", tb_musics);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_download:
                intent = new Intent(this, DownLoadActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_start_pause:
                if (iv_start_pause.getTag().equals("0")) {
                    iv_start_pause.setTag("1");
                    iv_start_pause.setImageResource(R.drawable.landscape_play_btn);
                    if (binder.getMediaPlayer() != null) {
                        binder.pause();
                    }

                } else {
                    iv_start_pause.setTag("0");
                    iv_start_pause.setImageResource(R.drawable.landscape_pause_btn);
                    if (binder.getMediaPlayer() != null) {
                        binder.start();
                    }
                }
                break;
        }
    }


    @Override
    public void onDownLoadSuccess() {
        try {
            tb_musics = (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
            adapter = new MusicListAdapter(this, tb_musics);
            lv_main.setAdapter(adapter);

            if (tb_musics == null || tb_musics.size() == 0) {
                iv_nothing.setVisibility(View.VISIBLE);
            } else {
                iv_nothing.setVisibility(View.GONE);
            }
            tv_download_num.setText("(" + DownLoadBean.downloadApis.size() + ")");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownLoadStart() {
        tv_download_num.setText("(" + DownLoadBean.downloadApis.size() + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownLoadMusicApi.downLoadFinishInterface = null;
        binder.stop();
        unbindService(conn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }


    public class PlayServiceVConnection implements ServiceConnection, PlayService2.OnMusicListener {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (PlayService2.MyBinder) iBinder;
            binder.setOnMusicListener(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }


        @Override
        public void nextMusic() {
            if (SearchMusicList.temp_beans!=null){
                if (currentPosition == SearchMusicList.temp_beans.size()-1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                tv_sound_name.setText(SearchMusicList.temp_beans.get(currentPosition).name);
                tv_singer_name.setText(SearchMusicList.temp_beans.get(currentPosition).singerName);
                musicListener.nextMusic(currentPosition);
            }else{
                if (currentPosition == tb_musics.size()-1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                tv_sound_name.setText(tb_musics.get(currentPosition).getSoungName());
                tv_singer_name.setText(tb_musics.get(currentPosition).getSingerName());
            }
            startPlayMusic(currentPosition);
        }

        @Override
        public void prevMusic() {
            if (SearchMusicList.temp_beans!=null){
                if (currentPosition == 0) {
                    currentPosition = SearchMusicList.temp_beans.size() - 1;
                } else {
                    currentPosition--;
                }
                tv_sound_name.setText(SearchMusicList.temp_beans.get(currentPosition).name);
                tv_singer_name.setText(SearchMusicList.temp_beans.get(currentPosition).singerName);
                musicListener.prevMusic(currentPosition);
            }else{
                if (currentPosition == 0) {
                    currentPosition = tb_musics.size() - 1;
                } else {
                    currentPosition--;
                }

                tv_sound_name.setText(tb_musics.get(currentPosition).getSoungName());
                tv_singer_name.setText(tb_musics.get(currentPosition).getSingerName());
            }
            startPlayMusic(currentPosition);
        }
    }

    private void startPlayMusic(int position) {
        binder.stop();
        if (SearchMusicList.temp_beans!=null){
            binder.setDataSource(SearchMusicList.temp_beans.get(position).mp3url);
        }else{
            binder.setDataSource(tb_musics.get(position).getMp3Url());
        }
        binder.prepare();
        binder.start();
    }

}
