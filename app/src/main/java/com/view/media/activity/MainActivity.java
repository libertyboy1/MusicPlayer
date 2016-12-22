package com.view.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.view.media.R;
import com.view.media.adapter.MusicListAdapter;
import com.view.media.api.DownLoadFinishInterface;
import com.view.media.api.DownLoadMusicApi;
import com.view.media.bean.DownLoadBean;
import com.view.media.constant.Constant;
import com.view.media.utils.FileUtil;
import com.view.media.view.ListSlideView;

import java.io.File;


public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, DownLoadFinishInterface {
    private ListSlideView lv_main;
    private ImageView iv_right;
    private ImageView iv_nothing;
    private LinearLayout ll_download;
    private TextView tv_download_num;

    private MusicListAdapter adapter;
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        super.initData();

        iv_right.setImageResource(R.mipmap.search);

        files = FileUtil.getFiles(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH);

        if (files == null || files.length == 0) {
            iv_nothing.setVisibility(View.VISIBLE);
        } else {
            iv_nothing.setVisibility(View.GONE);
        }

        tv_download_num.setText("(" + DownLoadBean.downloadApis.size() + ")");

        adapter = new MusicListAdapter(this, files);
        lv_main.setAdapter(adapter);
        tv_title.setText("本地音乐");
    }

    @Override
    public void initView() {
        super.initView();
        lv_main = (ListSlideView) findViewById(R.id.lv_main);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_nothing = (ImageView) findViewById(R.id.iv_nothing);
        ll_download = (LinearLayout) findViewById(R.id.ll_download);
        tv_download_num = (TextView) findViewById(R.id.tv_download_num);

    }

    @Override
    public void setListener() {
        super.setListener();
        lv_main.setOnItemClickListener(this);
        iv_right.setOnClickListener(this);
        ll_download.setOnClickListener(this);

        DownLoadMusicApi.downLoadFinishInterface = this;


        adapter.setRemoveListener(new MusicListAdapter.OnRemoveListener() {

            @Override
            public void onRemoveItem(int position) {
                // TODO 删除按钮的回调，注意也可以放在adapter里面处理，具体根据自己项目来
                files[position].delete();

                files = FileUtil.getFiles(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH);
                adapter = new MusicListAdapter(MainActivity.this, files);
                lv_main.setAdapter(adapter);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("position", i);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_right:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_download:
                intent = new Intent(this, DownLoadActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onDownLoadSuccess() {
        files = FileUtil.getFiles(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH);
        adapter = new MusicListAdapter(this, files);
        lv_main.setAdapter(adapter);

        if (files == null || files.length == 0) {
            iv_nothing.setVisibility(View.VISIBLE);
        } else {
            iv_nothing.setVisibility(View.GONE);
        }

        tv_download_num.setText("(" + DownLoadBean.downloadApis.size() + ")");
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
}
