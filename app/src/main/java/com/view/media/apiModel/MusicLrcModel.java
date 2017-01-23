package com.view.media.apiModel;

/**
 * Created by Destiny on 2016/12/20.
 */

public class MusicLrcModel {
    private String id;
    private String lv;
    private String kv;
    private String tv;
    private String name;
    private String url = "http://music.163.com/api/song/lyric";

    public String getId() {
        return id;
    }

    public String getLv() {
        return lv;
    }

    public String getKv() {
        return kv;
    }

    public String getTv() {
        return tv;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }


    public MusicLrcModel(String id, String lv, String kv, String tv,String name) {
        this.id = id;
        this.lv = lv;
        this.kv = kv;
        this.tv = tv;
        this.name=name;
    }


    public static class Builder {
        private String id;
        private String lv;
        private String kv;
        private String tv;
        private String name;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setLv(String lv) {
            this.lv = lv;
            return this;
        }

        public Builder setKv(String kv) {
            this.kv = kv;
            return this;
        }

        public Builder setTv(String tv) {
            this.tv = tv;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public MusicLrcModel create() {
            return new MusicLrcModel(id, "-1", "-1", "-1",name);
        }

    }

}
