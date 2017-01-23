package com.view.media.api;

import android.content.Context;
import android.util.Log;

import com.view.media.constant.Constant;
import com.view.media.apiModel.MusicLrcModel;
import com.view.media.utils.FileUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Destiny on 2016/12/20.
 */

public class MusicLrcApi {
    private Callback.Cancelable cancelable;
    private NetWorkStateListener listener;
    private Context mContext;
    private MusicLrcModel model;

    public MusicLrcApi(MusicLrcModel model, NetWorkStateListener listener, Context mContext) {
        this.listener = listener;
        this.mContext = mContext;
        this.model = model;
    }

    public void musicLrcApi() {

        RequestParams params = new RequestParams(model.getUrl());
        params.addBodyParameter("id", model.getId());
        params.addBodyParameter("lv", model.getLv());
        params.addBodyParameter("kv", model.getKv());
        params.addBodyParameter("tv", model.getTv());

        cancelable = x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject object = new JSONObject(result);

                    String lyric = object.getJSONObject("lrc").getString("lyric");
                    Log.e("JAVA", "onSuccess result:" + lyric);

                    FileUtil.writeSDFile(lyric, Constant.STR_SDCARD_PATH + Constant.STR_LRC_FILE_PATH, model.getName().trim()+ ".lrc");

                    if (listener != null) {
                        listener.onSuccess();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (listener != null) {
                    listener.onError();
                }
                ex.printStackTrace();
                Log.e("JAVA", "onError result:" + ex.getMessage() + "");
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
                if (listener != null) {
                    listener.onCancelled();
                }
                Log.e("JAVA", "onCancelled result:");
            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onFinished();
                }
                Log.e("JAVA", "onFinished result:");
            }
        });
    }

    public void cancel() {
        cancelable.cancel();
    }

}
