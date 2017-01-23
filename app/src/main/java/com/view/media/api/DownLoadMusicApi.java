package com.view.media.api;

import android.os.Handler;
import android.os.Message;

import com.view.media.bean.DownLoadBean;
import com.view.media.constant.Constant;
import com.view.media.db.DbManage;
import com.view.media.db.TableMusic;
import com.view.media.apiModel.DownLoadMusicModel;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;


/**
 * Created by Destiny on 2016/12/21.
 */

public class DownLoadMusicApi {
    public DownLoadMusicModel model;
    private Callback.Cancelable cancelable;
    public Handler handler;
    private String suffix;

    public static DownLoadFinishInterface downLoadFinishInterface;

    public DownLoadMusicApi(DownLoadMusicModel model) {
        this.model = model;
        suffix = model.getUrl().substring(model.getUrl().lastIndexOf("."), model.getUrl().length());
    }

    public void downloadMusic() {
        RequestParams requestParams = new RequestParams(model.getUrl());

        requestParams.setSaveFilePath(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH + "/" + model.getSongName() + suffix);
        cancelable = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                if (DownLoadBean.position != -1) {
                    DownLoadBean.downloadApis.remove(DownLoadBean.position);
                }

                DownLoadBean.downloadApis.add(DownLoadMusicApi.this);
                if (downLoadFinishInterface != null) {
                    downLoadFinishInterface.onDownLoadStart();
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                Log.e("----progress----", current + "  /  " + total);
                if (handler != null) {
                    Message message = Message.obtain();
                    message.obj = current + "," + total;
                    message.what = 0x1;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onSuccess(File result) {
                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
                if (downLoadFinishInterface != null) {
                    downLoadFinishInterface.onDownLoadSuccess();
                }
                if (handler != null) {
                    handler.sendEmptyMessage(0x2);
                }

                TableMusic tb_music = new TableMusic();
                tb_music.setAlbumName(model.getAlbumName());
                tb_music.setSingerName(model.getSingerName());
                tb_music.setSoungName(model.getSongName());
                tb_music.setMp3Url(model.getUrl());
                tb_music.setmId(model.getMid());
                tb_music.setMvId(model.getMvid());
                tb_music.setFilePath(Constant.STR_SDCARD_PATH + Constant.STR_MUSIC_FILE_PATH + "/" + model.getSongName() + suffix);
                tb_music.setLrcPath(Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH + "/" + model.getSongName() + ".lrc");
                try {
                    DbManage.manager.saveOrUpdate(tb_music);
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                DownLoadBean.downloadApis.remove(DownLoadMusicApi.this);
                if (handler != null) {
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

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

}
