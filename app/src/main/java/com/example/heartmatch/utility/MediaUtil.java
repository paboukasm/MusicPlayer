package com.example.heartmatch.utility;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.heartmatch.bean.Mp3Info;
import com.example.musicplayer.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class  MediaUtil {

    /*static final String API_KEY = "e325699e517ecfe9167e0b3397e0a646";
    static final String API_URL = "https://api.getsongbpm.com/search/?";
    String bpm;
    int nbpm;


    public int doInBackground(String s, String a) {
        String song = s;
        song = song.replaceAll("\\s","+");
        String artist = a;
        artist = artist.replaceAll("\\s","+");
        // Do some validation here

        try {
            URL url = new URL(API_URL + "api_key=" + API_KEY + "&type=both&lookup=song:" + song + "%20artist:" + artist);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                Log.d("json object", stringBuilder.toString());
//                    Log.d("bpm", stringBuilder.toString().tempo);
                JSONObject jObject = new JSONObject(stringBuilder.toString());
                JSONArray jArray = jObject.getJSONArray("search");

                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        bpm = oneObject.getString("tempo");
                        // String oneObjectsItem2 = oneObject.getString("anotherSTRINGNAMEINtheARRAY");
                        Log.d("BPM", bpm);
                    } catch (JSONException e) {
                        // Oops
                    }
                }

//                    Log.d("BPM PLEASE", oneOb);

//                    return stringBuilder.toString();

                nbpm = Integer.parseInt(bpm);
                return nbpm;
//                    return bpm;
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return Integer.parseInt(null);
        }
    } /*


    // Get album Uri
    /**
     * Check Song Info and Save in List
     *
     * @return
     */

    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static List<Mp3Info> getMp3Infos(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int mId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int mTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int mArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int mAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int mAlbumID = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        int mDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int mSize = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
        int mUrl = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int mIsMusic = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);

        List<Mp3Info> mp3Infos = new ArrayList<>();
        for (int i = 0, p = cursor.getCount(); i < p; i++) {
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(mId); // Music id
            String title = cursor.getString(mTitle); // Song Name
            String artist = cursor.getString(mArtist); // Aritist
            String album = cursor.getString(mAlbum); // Album
            long albumId = cursor.getInt(mAlbumID);
            long duration = cursor.getLong(mDuration); // Duration
            long size = cursor.getLong(mSize); // File Size
            String url = cursor.getString(mUrl); // File Path
            int isMusic = cursor.getInt(mIsMusic); // Music File or not
            if (isMusic != 0 && url.matches(".*\\.mp3$") && duration > 30 * 1000 && !artist.equals("<unknown>") && !artist.equals("")) { // Add only Music File to the List
                Log.d("song", "id:" + id + " title: " + title + " artist:" + artist + " album:" + album + " size:" + size + " duration: " + duration);
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
                //mp3Info.setBpm(doInBackground(String title, String artist));
            }
        }
        cursor.close();
        return mp3Infos;
    }

    /**
     * Map data to every element in the list, the mapped data should contain all Info of that song
     *
     * @param mp3Infos
     * @return
     */
    public static List<HashMap<String, String>> getMusicMaps(List<Mp3Info> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
            map.put("albumId", String.valueOf(mp3Info.getAlbumId()));
            map.put("duration", formatTime(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        return mp3list;
    }

    /**
     * Convert time format ms to min and sec
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     * get album pic
     *
     * @param context
     * @return
     */
    public static Bitmap getDefaultArtwork(Context context, boolean small) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if (small) { // return small pic, set default pic as album pic +
            return BitmapFactory.decodeStream(context.getResources().openRawResource(+R.drawable.icon_default_pink), null, opts);
        }
        return BitmapFactory.decodeStream(context.getResources().openRawResource(+R.drawable.icon_default_pink), null, opts);
    }

    /**
     * obtain album pic
     *
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // determine the size
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // size album pic with format:800pixel
            // use computeSampleSize to adjust the album pic size
            options.inSampleSize = 100;
            // obtain adjust ratio and load Bitmap data
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            // 根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * obtain the adjusted album pic
     *
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small) {
        if (album_id < 0) {
            if (song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefalut) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                // set the original size
                options.inSampleSize = 1;
                // compare the album pic size and the display view size
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 100);
                } else {
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                // Load Bitmap data
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtwork(context, small);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * adjust pic
     *
     * @param options
     * @param target
     * @return
     */
    public static int computeSampleSize(Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if (candidate == 0) {
            return 1;
        }
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

    /**
     * Board, ask system to scann for specific type of file
     */
    public static void scanSDCard(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Check SDK version
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
            intent.setData(Uri.parse("file://" + file.getAbsolutePath()));//MediaUtils.getMusicDir()
            context.sendBroadcast(intent);
        }

    }

    /**
     *Check if Service is connected
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


}



