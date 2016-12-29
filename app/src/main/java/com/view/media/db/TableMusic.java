package com.view.media.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Destiny on 2016/12/29.
 */
@Table(name="tb_music")
public class TableMusic implements Serializable{
    @Column(name = "id",isId=true)
    private int id;//主键id
    @Column(name = "mId")
    private String mId;//音乐ID
    @Column(name = "mvId")
    private String mvId;//音乐MVid
    @Column(name = "soungName")
    private String soungName;//音乐名字
    @Column(name="singerName")
    private String singerName;//歌手名字
    @Column(name = "albumName")
    private String albumName;//专辑名字
    @Column(name = "mp3Url")
    private String mp3Url;//音乐播放地址
    @Column(name = "filePath")
    private String filePath;//音乐本地播放地址
    @Column(name = "lrcPath")
    private String lrcPath;//音乐歌词本地地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getMvId() {
        return mvId;
    }

    public void setMvId(String mvId) {
        this.mvId = mvId;
    }

    public String getSoungName() {
        return soungName;
    }

    public void setSoungName(String soungName) {
        this.soungName = soungName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

 }
