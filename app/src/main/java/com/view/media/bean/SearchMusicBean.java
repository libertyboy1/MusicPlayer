package com.view.media.bean;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by Destiny on 2016/12/20.
 */

public class SearchMusicBean implements Serializable {
    public String name;
    public String mp3url;
    public String singerName;
    public String albumName;
    public String id;
    public String mvId;
    public boolean isDownload=false;
}
