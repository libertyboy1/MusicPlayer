package com.view.media.apiModel;

/**
 * Created by Destiny on 2016/12/20.
 */

public class SearchMusicModel {
    public int page;
    public String size;
    public String str_search;
    public String url="http://music.163.com/api/search/pc";

    public SearchMusicModel(String str_search, int page, String size) {
        this.str_search = str_search;
        this.page = page;
        this.size = size;
    }

}
