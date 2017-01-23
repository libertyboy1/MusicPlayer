package com.view.media.api;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.view.media.bean.MusicMvBean;
import com.view.media.apiModel.MusicMvModel;
import com.view.media.view.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Destiny on 2016/12/20.
 */

public class MusicMvApi implements DialogInterface.OnDismissListener {

    private MusicMvModel model;
    private Callback.Cancelable cancelable;
    private NetWorkStateListener listener;
    private ProgressDialog dialog;
    private Context mContext;
    private MusicMvBean bean;


    public MusicMvApi(MusicMvModel model, NetWorkStateListener listener, Context mContext) {
        this.listener = listener;
        this.model = model;
        this.mContext = mContext;
    }

    public void getMv(boolean isShowDialog) {
        dialog = ProgressDialog.createDialog(mContext);
        dialog.setMessage("数据加载中...");
        if (isShowDialog) {
            dialog.show();
        }

        dialog.setOnDismissListener(this);

        RequestParams params = new RequestParams(model.url);
        params.addHeader("Cookie", "appver=1.5.2");
        params.addHeader("Referer", "http://music.163.com/");

        params.setUseCookie(true);

        params.addBodyParameter("id", model.id);
        params.addBodyParameter("type", model.type);

        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("---sss--", result);

                try {
                    JSONObject json_rsule = new JSONObject(result);

                    String str_1080 = json_rsule.getJSONObject("data").getJSONObject("brs").has("1080")
                            ? json_rsule.getJSONObject("data").getJSONObject("brs").getString("1080") : "";

                    String str_720 = json_rsule.getJSONObject("data").getJSONObject("brs").has("720")
                            ? json_rsule.getJSONObject("data").getJSONObject("brs").getString("720") : "";

                    String str_480 = json_rsule.getJSONObject("data").getJSONObject("brs").has("480")
                            ? json_rsule.getJSONObject("data").getJSONObject("brs").getString("480") : "";

                    String str_240 = json_rsule.getJSONObject("data").getJSONObject("brs").has("240")
                            ? json_rsule.getJSONObject("data").getJSONObject("brs").getString("240") : "";

                    bean=new MusicMvBean();
                    bean.str_240=str_240;
                    bean.str_480=str_480;
                    bean.str_720=str_720;
                    bean.str_1080=str_1080;

                    if (listener != null) {
                        listener.onSuccess();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError();
                    }
                }

                dialog.dismiss();
            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (listener != null) {
                    listener.onError();
                }
                dialog.dismiss();
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
                if (listener != null) {
                    listener.onCancelled();
                }
                dialog.dismiss();
            }

            @Override
            public void onFinished() {
                if (listener != null) {
                    listener.onFinished();
                }
                dialog.dismiss();
            }
        });
    }

    public void cancel() {
        cancelable.cancel();
    }

    public MusicMvBean getBean() {
        return bean;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        cancel();
    }
}
