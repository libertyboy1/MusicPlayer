package com.view.media.apiModel;

/**
 * Created by Destiny on 2016/12/20.
 */

public class MusicMvModel {
    public String id;
    public String type="mp4";
    public String url="http://music.163.com/api/mv/detail";

    public MusicMvModel(String id) {
        this.id = id;
    }

}
