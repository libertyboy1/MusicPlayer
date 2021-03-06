package com.view.media.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.view.media.R;
import com.view.media.adapter.DownLoadAdapter;
import com.view.media.bean.DownLoadBean;
import com.view.media.view.ListSlideView;

/**
 * Created by Destiny on 2016/12/21.
 */

public class DownLoadActivity extends BaseActivity {
    private ListSlideView lv_main;
    private ImageView iv_nothing;


    private DownLoadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    public void initData() {
        super.initData();

        mToolbar.setTitle("下载管理");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);

        adapter = new DownLoadAdapter(this);
        lv_main.setAdapter(adapter);

        if (DownLoadBean.downloadApis.size() == 0) {
            iv_nothing.setVisibility(View.VISIBLE);
        } else {
            iv_nothing.setVisibility(View.GONE);
        }

        initToolBar("下载管理",true);

    }

    @Override
    public void initView() {
        super.initView();
        lv_main = (ListSlideView) findViewById(R.id.lv_main);
        iv_nothing = (ImageView) findViewById(R.id.iv_nothing);
    }

}
