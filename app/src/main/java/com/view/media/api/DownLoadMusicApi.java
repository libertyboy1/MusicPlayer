package com.view.media.api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.view.media.bean.DownLoadBean;
import com.view.media.constant.Constant;
import com.view.media.model.DownLoadMusicModel;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import static org.xutils.x.http;


/**
 * Created by Destiny on 2016/12/21.
 */

public class DownLoadMusicApi {
    public DownLoadMusicModel model;
    private Callback.Cancelable cancelable;
    public Handler handler;

    public static DownLoadFinishInterface downLoadFinishInterface;

    public DownLoadMusicApi(DownLoadMusicModel model) {
        this.model = model;
    }

    public void downloadMusic() {
        RequestParams requestParams = new RequestParams(model.getUrl());
        String suffix = model.getUrl().substring(model.getUrl().lastIndexOf("."), model.getUrl().length());
        requestParams.setSaveFilePath(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH + "/" + model.getSingerName() + "|" + model.getSongName() + "|" + model.getAlbumName() + suffix);
        cancelable = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                if (DownLoadBean.position!=-1){
                    DownLoadBean.downloadApis.remove(DownLoadBean.position);
                }

                DownLoadBean.downloadApis.add(DownLoadMusicApi.this);
                if (downLoadFinishInterface!=null){
                    downLoadFinishInterface.onDownLoadStart();
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                Log.e("----progress----", current + "  /  " + total);
                if (handler!=null){
                    Message message=Message.obtain();
                    message.obj= current + "," + total;
                    message.what=0x1;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onSuccess(File result) {
                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
                if (downLoadFinishInterface!=null){
                    downLoadFinishInterface.onDownLoadSuccess();
                }
                if (handler!=null){
                    handler.sendEmptyMessage(0x2);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
                if (handler!=null){
                    handler.sendEmptyMessage(0x3);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
            }

            @Override
            public void onFinished() {
//                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
            }
        });
    }

    public void cancel() {
        cancelable.cancel();
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

}
