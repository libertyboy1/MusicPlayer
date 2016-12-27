package com.view.media.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.view.media.R;

import static android.os.Build.VERSION_CODES.M;
import static com.view.media.bean.DownLoadBean.position;

/**
 * Created by Destiny on 2016/12/19.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public Toolbar mToolbar;

    public Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setListener();
    }

    public void setListener() {
    }

    public void initToolBar(String title, boolean isShowBack) {
        mToolbar.setTitle(title);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        if (isShowBack) {
            mToolbar.setNavigationIcon(R.mipmap.back);
        }
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = new Intent();
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    intent.setClass(BaseActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.action_download:
                    ((PlayActivity) BaseActivity.this).downLoadMusicApi.downloadMusic();
                    break;
            }
            return true;
        }
    };

    public void initData() {
        gson = new Gson();
    }

    public void initView() {
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
    }

    @Override
    public void onClick(View view) {
    }
}
