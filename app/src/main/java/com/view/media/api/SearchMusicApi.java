package com.view.media.api;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.view.media.bean.SearchMusicBean;
import com.view.media.model.SearchMusicModel;
import com.view.media.view.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Destiny on 2016/12/20.
 */

public class SearchMusicApi implements DialogInterface.OnDismissListener {

    private SearchMusicModel model;
    private Callback.Cancelable cancelable;
    private NetWorkStateListener listener;
    private ProgressDialog dialog;
    private Context mContext;
    private ArrayList<SearchMusicBean> beans;


    public SearchMusicApi(SearchMusicModel model, NetWorkStateListener listener, Context mContext) {
        this.listener = listener;
        this.model = model;
        this.mContext = mContext;
    }

    public void searchMusic(boolean isShowDialog) {
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

        params.addBodyParameter("s", model.str_search + "");
        params.addBodyParameter("limit", model.size);
        params.addBodyParameter("type", "1");
        params.addBodyParameter("offset", model.page + "");


//        params.addBodyParameter("page", model.getPage() + "");
//        params.addBodyParameter("size", model.getSize());
//        params.addBodyParameter("q", model.getStr_search());

        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                beans = new ArrayList<SearchMusicBean>();
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray songs = object.getJSONObject("result").getJSONArray("songs");

                    for (int position = 0; position < songs.length(); position++) {

                        String songName = songs.getJSONObject(position).getString("name");
                        String mp3Url = songs.getJSONObject(position).getString("mp3Url");
                        JSONArray singerNames = songs.getJSONObject(position).getJSONArray("artists");
                        String albumName = songs.getJSONObject(position).getJSONObject("album").getString("name");
                        String id = songs.getJSONObject(position).getString("id");
                        String mvId = songs.getJSONObject(position).getString("mvid");

                        StringBuffer sb_singer = new StringBuffer();
                        for (int i = 0; i < singerNames.length(); i++) {
                            if (i == singerNames.length() - 1) {
                                sb_singer.append(singerNames.getJSONObject(i).getString("name"));
                            } else {
                                sb_singer.append(singerNames.getJSONObject(i).getString("name") + ",");
                            }
                        }

                        SearchMusicBean bean = new SearchMusicBean();
                        bean.albumName = albumName;
                        bean.mp3url = mp3Url;
                        bean.name = songName;
                        bean.singerName = sb_singer.toString();
                        bean.id = id;
                        bean.mvId=mvId;
                        beans.add(bean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onSuccess();
                dialog.dismiss();
            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onError();
                dialog.dismiss();
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
                listener.onCancelled();
                dialog.dismiss();
            }

            @Override
            public void onFinished() {
                listener.onFinished();
                dialog.dismiss();
            }
        });
    }

    public void cancel() {
        cancelable.cancel();
    }

    public ArrayList<SearchMusicBean> getBean() {
        return beans;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        cancel();
    }
}
