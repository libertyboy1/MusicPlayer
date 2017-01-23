package com.view.media.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.adapter.SearchMusicListAdapter;
import com.view.media.api.NetWorkStateListener;
import com.view.media.api.SearchMusicApi;
import com.view.media.bean.SearchMusicBean;
import com.view.media.bean.SearchMusicList;
import com.view.media.db.DbManage;
import com.view.media.db.TableMusic;
import com.view.media.apiModel.SearchMusicModel;
import com.view.media.view.ProgressView;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import me.fangx.haorefresh.HaoRecyclerView;
import me.fangx.haorefresh.LoadMoreListener;

import static com.view.media.bean.SearchMusicList.temp_beans;

/**
 * Created by Destiny on 2016/12/20.
 */

public class SearchActivity extends BaseActivity implements SearchMusicListAdapter.OnItemClickListener, NetWorkStateListener {

    private SwipeRefreshLayout swiperefresh;
    private HaoRecyclerView hao_recycleview;

    private ImageView iv_nothing;
    private TextView tv_search;
    private EditText et_search;

    private SearchMusicModel model;
    private SearchMusicApi api;
    private ArrayList<SearchMusicBean> beans;
    private SearchMusicListAdapter adapter;

    private int currentPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        super.initData();
        SearchMusicList.temp_beans=new ArrayList<SearchMusicBean>();
        initToolBar("搜索音乐", true);

        MainActivity.musicListener=new MainActivity.MusicListener() {
            @Override
            public void nextMusic(int position) {
                startPlayMusic(position);
            }

            @Override
            public void prevMusic(int position) {
                startPlayMusic(position);
            }
        };

    }

    @Override
    public void initView() {
        super.initView();
        iv_nothing = (ImageView) findViewById(R.id.iv_nothing);
        tv_search = (TextView) findViewById(R.id.tv_search);
        et_search = (EditText) findViewById(R.id.et_search);
        hao_recycleview = (HaoRecyclerView) findViewById(R.id.hao_recycleview);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hao_recycleview.setLayoutManager(layoutManager);

        //设置自定义加载中和到底了效果
        ProgressView progressView = new ProgressView(this);
        progressView.setIndicatorId(ProgressView.BallPulse);
        progressView.setIndicatorColor(0xff69b3e0);
        hao_recycleview.setFootLoadingView(progressView);

        TextView textView = new TextView(this);
        textView.setText("已经到底啦~");
        textView.setTextColor(Color.WHITE);
        hao_recycleview.setFootEndView(textView);


    }

    @Override
    public void setListener() {
        super.setListener();
        tv_search.setOnClickListener(this);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model.page = 1;
                api.searchMusic(false);
            }
        });

        hao_recycleview.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                model.page++;
                api.searchMusic(false);
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_search:
                model = new SearchMusicModel(et_search.getText().toString().trim(), 1, "20");
                api = new SearchMusicApi(model, this, this);
                api.searchMusic(true);
                break;
        }
    }

    @Override
    public void onSuccess() {
        beans = api.getBean();

        for (SearchMusicBean bean : beans) {
            try {
                List<TableMusic> tb_musics = DbManage.manager.selector(TableMusic.class).where("mId", "=", bean.id).findAll();
                if (tb_musics != null && tb_musics.size() > 0) {
                    bean.isDownload = true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (model.page == 1) {
            SearchMusicList.temp_beans.clear();
            if (beans.size() == 0) {
                iv_nothing.setVisibility(View.VISIBLE);
            } else {
                iv_nothing.setVisibility(View.GONE);
            }
        }

        if (beans != null && beans.size() > 0) {
            SearchMusicList.temp_beans.addAll(beans);
        } else {
            hao_recycleview.loadMoreEnd();
            return;
        }

        if (adapter == null) {
            adapter = new SearchMusicListAdapter(this, SearchMusicList.temp_beans);
            hao_recycleview.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        } else {
            adapter.notifyDataSetChanged();

            if (model.page == 1) {
                hao_recycleview.refreshComplete();
                swiperefresh.setRefreshing(false);
            } else {
                hao_recycleview.loadMoreComplete();
            }
        }

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


    private void startPlayMusic(int position) {
        MainActivity.binder.stop();
        MainActivity.binder.setDataSource(SearchMusicList.temp_beans.get(position).mp3url);
        MainActivity.binder.prepare();
        MainActivity.binder.start();
    }


    @Override
    public void onItemClick(View v, final int position) {
        if (currentPosition!=position){
            startPlayMusic(position);
            MainActivity.tv_sound_name.setText(SearchMusicList.temp_beans.get(position).name);
            MainActivity.tv_singer_name.setText(SearchMusicList.temp_beans.get(position).singerName);
            MainActivity. binder.getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("SearchActivity", "开始播放");
                    Intent intent = new Intent(SearchActivity.this, PlayActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("bean", SearchMusicList.temp_beans);
                    startActivity(intent);
                }
            });
        }else{
            Intent intent = new Intent(SearchActivity.this, PlayActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("bean", SearchMusicList.temp_beans);
            startActivity(intent);
        }


    }
}
