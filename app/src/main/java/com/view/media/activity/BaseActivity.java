package com.view.media.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.view.media.R;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Destiny on 2016/12/19.
 */

public class BaseActivity extends Activity implements View.OnClickListener{
    private ImageView iv_back,iv_more;
    public TextView tv_title;
    public Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setListener();
    }

    public void setListener() {
        iv_back.setOnClickListener(this);
    }

    public void initData() {
        gson=new Gson();
    }

    public void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        tv_title= (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
