package com.view.media.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.view.media.R;
import com.view.media.api.MusicMvApi;
import com.view.media.api.NetWorkStateListener;
import com.view.media.bean.MusicMvBean;
import com.view.media.apiModel.MusicMvModel;
import com.view.media.view.VideoView;


/**
 * Created by Destiny on 2016/12/28.
 */

public class MvActivity extends BaseActivity implements NetWorkStateListener,View.OnClickListener {
    private VideoView vv_main;

    private MusicMvApi mvApi;
    private MusicMvBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mv;
    }

    @Override
    public void initData() {
        super.initData();

        MusicMvModel mvModel = new MusicMvModel(getIntent().getStringExtra("mvid"));
        mvApi = new MusicMvApi(mvModel, this, this);
        mvApi.getMv(true);

        vv_main.setTitle(getIntent().getStringExtra("soundName"));

    }

    @Override
    public void initView() {
        super.initView();
        vv_main = (VideoView) findViewById(R.id.vv_main);
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    @Override
    public void onSuccess() {
        bean = mvApi.getBean();
        Log.e("-----",bean.str_240);
        vv_main.setVideoUri(Uri.parse(bean.str_240));
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
        }
    }
}
