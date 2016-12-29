package com.view.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.adapter.MusicListAdapter;
import com.view.media.api.DownLoadFinishInterface;
import com.view.media.api.DownLoadMusicApi;
import com.view.media.bean.DownLoadBean;
import com.view.media.db.DbManage;
import com.view.media.db.TableMusic;
import com.view.media.view.ListSlideView;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, DownLoadFinishInterface {
    private ListSlideView lv_main;
    private ImageView iv_nothing;
    private LinearLayout ll_download;
    private TextView tv_download_num;

    private MusicListAdapter adapter;
    private ArrayList<TableMusic> tb_musics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void initData() {
        super.initData();

        try {
            tb_musics= (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
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

        initToolBar("",false);
        mToolbar.setLogo(R.mipmap.icon);

    }

    @Override
    public void initView() {
        super.initView();
        lv_main = (ListSlideView) findViewById(R.id.lv_main);
        iv_nothing = (ImageView) findViewById(R.id.iv_nothing);
        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        tv_download_num = (TextView) findViewById(R.id.tv_download_num);
    }

    @Override
    public void setListener() {
        super.setListener();
        lv_main.setOnItemClickListener(this);
        ll_download.setOnClickListener(this);

        DownLoadMusicApi.downLoadFinishInterface = this;

        adapter.setRemoveListener(new MusicListAdapter.OnRemoveListener() {

            @Override
            public void onRemoveItem(int position) {
                try {
                    DbManage.manager.delete(tb_musics.get(position));
                    new File(tb_musics.get(position).getFilePath()).delete();
                    tb_musics= (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
                    adapter = new MusicListAdapter(MainActivity.this, tb_musics);
                    lv_main.setAdapter(adapter);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("position", i);
        intent.putExtra("tb_musics",tb_musics);
        startActivity(intent);
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
        }
    }


    @Override
    public void onDownLoadSuccess() {
        try {
            tb_musics= (ArrayList<TableMusic>) DbManage.manager.selector(TableMusic.class).findAll();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }

}
