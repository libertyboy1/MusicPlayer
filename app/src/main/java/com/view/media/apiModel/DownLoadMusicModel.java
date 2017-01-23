package com.view.media.apiModel;

/**
 * Created by Destiny on 2016/12/21.
 */

public class DownLoadMusicModel {
    private String url;
    private String songName;
    private String singerName;
    private String albumName;
    private String type;
    private String mvid;
    private String mid;

    public DownLoadMusicModel(String url, String songName, String singerName, String albumName, String type, String mvid, String mid) {
        this.url = url;
        this.songName = songName;
        this.singerName = singerName;
        this.albumName = albumName;
        this.type = type;
        this.mvid = mvid;
        this.mid = mid;
    }

    public String getUrl() {
        return url;
    }


    public String getSongName() {
        return songName;
    }


    public String getSingerName() {
        return singerName;
    }


    public String getAlbumName() {
        return albumName;
    }


    public String getType() {
        return type;
    }

    public String getMvid() {
        return mvid;
    }

    public String getMid() {
        return mid;
    }


    public static class Builder {
        private String url;
        private String songName;
        private String singerName;
        private String albumName;
        private String type;
        private String mvid;
        private String mid;

        public Builder() {

        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSongName(String songName) {
            this.songName = songName;
            return this;
        }

        public Builder setSingerName(String singerName) {
            this.singerName = singerName;
            return this;
        }

        public Builder setAlbumName(String albumName) {
            this.albumName = albumName;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setMvid(String mvid) {
            this.mvid = mvid;
            return this;
        }

        public Builder setMid(String mid) {
            this.mid = mid;
            return this;
        }

        public DownLoadMusicModel create() {
            return new DownLoadMusicModel(url, songName, singerName, albumName, type, mvid, mid);
        }
    }

}
