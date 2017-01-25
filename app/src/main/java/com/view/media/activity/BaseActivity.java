package com.view.media.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.view.media.R;

import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.M;
import static com.view.media.bean.DownLoadBean.position;

/**
 * Created by Destiny on 2016/12/19.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public Toolbar mToolbar;

    public Gson gson;

    private int layoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        setListener();
    }

    public int getLayoutId() {
        return layoutId;
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
