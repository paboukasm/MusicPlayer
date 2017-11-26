package com.example.heartmatch.utility;

/**
 * @Author haopi
 * @Date 2016-09-06
 * @Des TODO
 */
public class Constants {
    //musicservice's name
    public static final String MUSIC_SERVICE = "MusicService";
    //Location Song listview
    public static final String ACTION_LIST_ITEM = "com.example.musicplayer.listitem";
    //Pause Music
    public static final String ACTION_PAUSE = "com.example.musicplayer.pause";
    //播Play Music
    public static final String ACTION_PLAY = "com.example.musicplayer.play";
    //Next Song
    public static final String ACTION_NEXT = "com.example.musicplayer.next";
    //previous Song
    public static final String ACTION_PRV = "com.example.musicplayer.prv";

    public static final String ACTION_CLOSE = "com.example.musicplayer.close";
    //Manual Control seekbar
    public static final String ACTION_SEEK ="com.example.musicplayer.seek";
    //When Operation is finished
    public static final String ACTION_COMPLETION = "com.example.musicplayer.completion";

    public static final int MSG_PROGRESS = 001;
    public static final int MSG_PREPARED = 002;
    public static final int MSG_PLAY_STATE = 003;
    // Cancel
    public static final int MSG_CANCEL = 004;

    public static final String URL_GET_MUSIC_ID = "http://s.music.163.com/search/get/?src=lofter&type=1&filterDj=true&s=";
    public static final String URl_GET_MUSIC_LRC = "http://music.163.com/api/song/media?id=";

    public static final int NOTIFICATION_CEDE = 100;
}
