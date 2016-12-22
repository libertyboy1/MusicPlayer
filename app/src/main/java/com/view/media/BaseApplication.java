package com.view.media;

import android.app.Application;

import com.view.media.constant.Constant;
import com.view.media.utils.FileUtil;

import org.xutils.x;

import static android.R.attr.key;

/**
 * Created by Destiny on 2016/12/20.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initFile();
        x.Ext.init(this);
    }

    private void initFile() {
        FileUtil.createFile(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH);
        FileUtil.createFile(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH);
    }

}
