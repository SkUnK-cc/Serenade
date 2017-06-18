package com.example.serenade.serenade.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Serenade on 17/6/18.
 */

public class Song extends RealmObject implements Serializable {
    /**
     * m4a : http://ws.stream.qqmusic.qq.com/498307.m4a?fromtag=46
     * media_mid : 0024MWM52SVYgg
     * songid : 498307
     * singerid : 2
     * albumname : 遥望
     * downUrl : http://dl.stream.qqmusic.qq.com/498307.m4a?vkey=ACC38ED0DCF95DB5721B7DC3ADA77ACB6DD9650F8487AF9E3C5D0634B09B39C5FFD2C6F42250D3E439B5F106741905BA60EE85024CA674E5&guid=2718671044
     * singername : BEYOND
     * songname : 海阔天空
     * strMediaMid : 0024MWM52SVYgg
     * albummid : 004Z88hS1FiU07
     * songmid : 003Dk8AU00uTPp
     * albumpic_big : http://i.gtimg.cn/music/photo/mid_album_300/0/7/004Z88hS1FiU07.jpg
     * albumpic_small : http://i.gtimg.cn/music/photo/mid_album_90/0/7/004Z88hS1FiU07.jpg
     * albumid : 40035
     */

    private String savePath;//音乐文件保存路径
    private String lyricPath;//歌词保存路径
    private int duration;//歌曲时长

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    private String m4a;
    private String media_mid;
    @PrimaryKey
    private int songid;
    private int singerid;
    private String albumname;
    private String downUrl;
    private String singername;
    private String songname;
    private String strMediaMid;
    private String albummid;
    private String songmid;
    private String albumpic_big;
    private String albumpic_small;
    private int albumid;

    public String getM4a() {
        return m4a;
    }

    public void setM4a(String m4a) {
        this.m4a = m4a;
    }

    public String getMedia_mid() {
        return media_mid;
    }

    public void setMedia_mid(String media_mid) {
        this.media_mid = media_mid;
    }

    public int getSongid() {
        return songid;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }

    public int getSingerid() {
        return singerid;
    }

    public void setSingerid(int singerid) {
        this.singerid = singerid;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getStrMediaMid() {
        return strMediaMid;
    }

    public void setStrMediaMid(String strMediaMid) {
        this.strMediaMid = strMediaMid;
    }

    public String getAlbummid() {
        return albummid;
    }

    public void setAlbummid(String albummid) {
        this.albummid = albummid;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public String getAlbumpic_big() {
        return albumpic_big;
    }

    public void setAlbumpic_big(String albumpic_big) {
        this.albumpic_big = albumpic_big;
    }

    public String getAlbumpic_small() {
        return albumpic_small;
    }

    public void setAlbumpic_small(String albumpic_small) {
        this.albumpic_small = albumpic_small;
    }

    public int getAlbumid() {
        return albumid;
    }

    public void setAlbumid(int albumid) {
        this.albumid = albumid;
    }
}
