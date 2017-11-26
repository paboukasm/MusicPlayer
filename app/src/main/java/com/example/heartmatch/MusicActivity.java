package com.example.heartmatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartmatch.bean.Mp3Info;
import com.example.heartmatch.service.MusicService;
import com.example.heartmatch.utility.Constants;
import com.example.heartmatch.utility.MediaUtil;
import com.example.heartmatch.utility.SpTools;
import com.example.heartmatch.utility.StatusBarUtil;
import com.example.heartmatch.view.SlidingMenu;
import com.example.musicplayer.R;
import com.example.musicview.MusicPlayerView;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MusicActivity";
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private MusicPlayerView mpv;
    private RelativeLayout mainView;
    private ListView mLeftView;
    private ImageView mNext;
    private ImageView mPrevious;
    private List<Mp3Info> mMusicList = new ArrayList<>();
    private ImageView mIv_back;
    private SlidingMenu mSlidingMenu;
    private TextView mSong;
    private TextView mSinger;
    private ImageView mPlayMode;
    private ImageView mRecommend;
    private ImageView mGesture;
    private int mPosition;
    private boolean mIsPlaying = false;

    private RemoteViews remoteViews;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Mp3Info mMp3Info;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constants.MSG_PROGRESS) {
                int currentPosition = msg.arg1;
                int totalDuration = msg.arg2;
                mpv.setProgress(currentPosition);
                mpv.setMax(totalDuration);
            }
            if (msg.what == Constants.MSG_PREPARED) {
                mPosition = msg.arg1;
                mIsPlaying = (boolean) msg.obj;
                switchSongUI(mPosition, mIsPlaying);
            }
            if (msg.what == Constants.MSG_PLAY_STATE) {
                mIsPlaying = (boolean) msg.obj;
                refreshPlayStateUI(mIsPlaying);
            }
            if (msg.what == Constants.MSG_CANCEL) {
                mIsPlaying = false;
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initPermission();
    }

    private void init(){
        initData();
        initEvent();
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // no premission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
            }
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                // Permission Denied
                Toast.makeText(MusicActivity.this, "Permission Denied, Music can't run.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("InlinedApi")
    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtil.enableTranslucentStatusbar(this);
        setContentView(R.layout.activity_main);
        mainView = (RelativeLayout) findViewById(R.id.main_bg);
        mSlidingMenu = (SlidingMenu) findViewById(R.id.sm);
        // left
        mLeftView = (ListView) findViewById(R.id.listviewL);
        // content
        mIv_back = (ImageView) findViewById(R.id.library);//left menu
        mSong = (TextView) findViewById(R.id.textViewSong);//song name
        mSinger = (TextView) findViewById(R.id.textViewSinger);//artist
        mpv = (MusicPlayerView) findViewById(R.id.mpv);//play/pause button
        mPrevious = (ImageView) findViewById(R.id.previous);//previous
        mPlayMode = (ImageView) findViewById(R.id.play_mode);//play mode
        mNext = (ImageView) findViewById(R.id.next);//next
        mRecommend = (ImageView) findViewById(R.id.recommend); //recommend
        mGesture = (ImageView) findViewById(R.id.gesturebutton); //gesture
        remoteViews = new RemoteViews(getPackageName(), R.layout.customnotice);//layout view
        createNotification();//notification bar
    }

    private void initData() {
        //music list
        mMusicList = MediaUtil.getMp3Infos(this);
        //active music service
        startMusicService();
        //message notification
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mLeftView.setAdapter(new MediaListAdapter());
        //initialize UI, display the song where it stop for the last use
        mPosition = SpTools.getInt(getApplicationContext(), "music_current_position", 0);
        mIsPlaying = MusicService.isPlaying();
        switchSongUI(mPosition, mIsPlaying);
    }

    /**
     * Start Music Service
     */
    private void startMusicService() {
        Intent musicService = new Intent();
        musicService.setClass(getApplicationContext(), MusicService.class);
        musicService.putParcelableArrayListExtra("music_list", (ArrayList<? extends Parcelable>) mMusicList);
        musicService.putExtra("messenger", new Messenger(handler));
        startService(musicService);
    }

    /**
     *Refresh the Song Info when song switched
     */
    private void switchSongUI(int position, boolean isPlaying) {
        if (mMusicList.size() > 0 && position < mMusicList.size()) {
            // 1.get data
            mMp3Info = mMusicList.get(position);
            // 2.set song name , artist
            String mSongTitle = mMp3Info.getTitle();
            String mSingerArtist = mMp3Info.getArtist();
            mSong.setText(mSongTitle);
            mSinger.setText(mSingerArtist);
            // 3.update notification bar and UI
            Bitmap mBitmap = MediaUtil.getArtwork(MusicActivity.this, mMp3Info.getId(), mMp3Info.getAlbumId(), true, false);
            remoteViews.setImageViewBitmap(R.id.widget_album, mBitmap);
            remoteViews.setTextViewText(R.id.widget_title, mMp3Info.getTitle());
            remoteViews.setTextViewText(R.id.widget_artist, mMp3Info.getArtist());
            refreshPlayStateUI(isPlaying);
            mpv.setCoverBitmap(mBitmap);
            // 4.set layoutpage background
            assert mBitmap != null;
            Palette.from(mBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette p) {
                    int mutedColor = p.getMutedColor(Color.BLACK);
                    Palette.Swatch darkMutedSwatch = p.getDarkMutedSwatch();
                    mainView.setBackgroundColor(darkMutedSwatch != null ? darkMutedSwatch.getRgb() : mutedColor);
                    mLeftView.setBackgroundColor(darkMutedSwatch != null ? darkMutedSwatch.getRgb() : mutedColor);
                }
            });
            // 5.change the color of the name of the current playing on the list
            changeColorNormalPrv();
            changeColorSelected();
        }
    }

    /**
     *refresh ui and notification
     */
    private void refreshPlayStateUI(boolean isPlaying) {
        updateMpv(isPlaying);
        updateNotification();
    }

    /**
     updat play/pause button
     */
    private void updateMpv(boolean isPlaying) {
        // content播放控件
        if (isPlaying) {
            mpv.start();
        } else {
            mpv.stop();
        }

    }

    /**
     * update UI
     */
    private void updateNotification() {
        Intent intent_play_pause;
        // set notification bar
        if (mIsPlaying) {
            remoteViews.setImageViewResource(R.id.widget_play, R.drawable.widget_play);
        } else {
            remoteViews.setImageViewResource(R.id.widget_play, R.drawable.widget_pause);
        }
        // set play
        if (mIsPlaying) {//if playing -> stop
            intent_play_pause = new Intent();
            intent_play_pause.setAction(Constants.ACTION_PAUSE);
            PendingIntent pending_intent_play = PendingIntent.getBroadcast(this, 4, intent_play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, pending_intent_play);
        }
        if (!mIsPlaying) {//if pause -> play
            intent_play_pause = new Intent();
            intent_play_pause.setAction(Constants.ACTION_PLAY);
            PendingIntent pending_intent_play = PendingIntent.getBroadcast(this, 5, intent_play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, pending_intent_play);
        }
        mNotificationManager.notify(Constants.NOTIFICATION_CEDE, mBuilder.build());
    }

    /**
     * set notification
     */
    @SuppressLint("NewApi")
    private void createNotification() {
        mBuilder = new NotificationCompat.Builder(this);

        Intent intent_main = new Intent(this, MusicActivity.class);
        PendingIntent pending_intent_go = PendingIntent.getActivity(this, 1, intent_main, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notice, pending_intent_go);

        Intent intent_cancel = new Intent();
        intent_cancel.setAction(Constants.ACTION_CLOSE);
        PendingIntent pending_intent_close = PendingIntent.getBroadcast(this, 2, intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_close, pending_intent_close);

        // previous
        Intent intent_prv = new Intent();
        intent_prv.setAction(Constants.ACTION_PRV);
        PendingIntent pending_intent_prev = PendingIntent.getBroadcast(this, 3, intent_prv, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev, pending_intent_prev);

        // play/pause
        Intent intent_play_pause;
        if (mIsPlaying) {//if playing -> stop
            intent_play_pause = new Intent();
            intent_play_pause.setAction(Constants.ACTION_PAUSE);
            PendingIntent pending_intent_play = PendingIntent.getBroadcast(this, 4, intent_play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, pending_intent_play);
        }
        if (!mIsPlaying) {///if pause -> play
            intent_play_pause = new Intent();
            intent_play_pause.setAction(Constants.ACTION_PLAY);
            PendingIntent pending_intent_play = PendingIntent.getBroadcast(this, 5, intent_play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, pending_intent_play);
        }

        // next
        Intent intent_next = new Intent();
        intent_next.setAction(Constants.ACTION_NEXT);
        PendingIntent pending_intent_next = PendingIntent.getBroadcast(this, 6, intent_next, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next, pending_intent_next);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher); // set icon on the notification bar
        mBuilder.setContent(remoteViews);
        mBuilder.setOngoing(true);
    }

    private void initEvent() {
        mIv_back.setOnClickListener(this);
        mpv.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mPlayMode.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mRecommend.setOnClickListener(this);
        mGesture.setOnClickListener(this);
        mLeftView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //click left menu
                changeColorNormal();
                sendBroadcast(Constants.ACTION_LIST_ITEM, i);
                mSlidingMenu.switchMenu(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.library://left menu
                mSlidingMenu.toggle();
                break;
            case R.id.mpv:// play/pause
                if (mIsPlaying) {
                    sendBroadcast(Constants.ACTION_PAUSE);
                } else {
                    sendBroadcast(Constants.ACTION_PLAY);
                }
                break;
            case R.id.previous:
                sendBroadcast(Constants.ACTION_PRV);
                break;
            case R.id.play_mode://switch play mode
                MusicService.playMode++;
                switch (MusicService.playMode % 3) {
                    case 0:
                        mPlayMode.setImageResource(R.drawable.player_btn_mode_shuffle_normal);
                        break;
                    case 1:
                        mPlayMode.setImageResource(R.drawable.player_btn_mode_loopsingle_normal);
                        break;
                    case 2:
                        mPlayMode.setImageResource(R.drawable.player_btn_mode_playall_normal);
                        break;
                }
                break;
            case R.id.next:
                sendBroadcast(Constants.ACTION_NEXT);
                break;

            case R.id.recommend:
                Intent open = new Intent(MusicActivity.this,HeartRateActivity.class);
                startActivity(open);
                break;

            case R.id.gesturebutton:
                Intent open1 = new Intent (MusicActivity.this,GestureActivity.class);
                startActivity(open1);
                break;
        }
    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action, int position) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.setAction(action);
        sendBroadcast(intent);
    }

    /**
     * left menu adapter
     */
    private class MediaListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMusicList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMusicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(MusicActivity.this, R.layout.music_listitem, null);
                holder.mImgAlbum = (ImageView) convertView.findViewById(R.id.img_album);
                holder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.mTvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mImgAlbum.setImageBitmap(MediaUtil.getArtwork(MusicActivity.this, mMusicList.get(position).getId(), mMusicList.get(position).getAlbumId(), true, true));
            holder.mTvTitle.setText(mMusicList.get(position).getTitle());
            holder.mTvArtist.setText(mMusicList.get(position).getArtist());

            if (mPosition == position) {
                holder.mTvTitle.setTextColor(getResources().getColor(R.color.colorAccent));
            } else {
                holder.mTvTitle.setTextColor(getResources().getColor(R.color.colorNormal));
            }
            holder.mTvTitle.setTag(position);

            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView mImgAlbum;
        TextView mTvTitle;
        TextView mTvArtist;
    }

    public void changeColorNormal() {
        TextView tv = (TextView) mLeftView.findViewWithTag(mPosition);
        if (tv != null) {
            tv.setTextColor(getResources().getColor(R.color.colorNormal));
        }
    }

    public void changeColorNormalPrv() {
        TextView tv = (TextView) mLeftView.findViewWithTag(MusicService.prv_position);
        if (tv != null) {
            tv.setTextColor(getResources().getColor(R.color.colorNormal));
        }
    }

    public void changeColorSelected() {
        TextView tv = (TextView) mLeftView.findViewWithTag(mPosition);
        if (tv != null) {
            tv.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpTools.setInt(getApplicationContext(), "music_current_position", mPosition);
    }

}
