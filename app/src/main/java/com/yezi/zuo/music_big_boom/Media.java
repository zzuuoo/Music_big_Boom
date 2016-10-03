package com.yezi.zuo.music_big_boom;

/**
 * Created by zuo on 2016/10/2.
 */
public class Media {
    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    private String display_name;//歌曲文件名称
    private String name;//歌名
    private long time;//时长
    private String Album;//专辑
    private String Artist;//歌手
    private long Id;
    private String data;//路径

    public String getArtist() {
        return Artist;
    }

    public String getData() {
        return data;    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

}
