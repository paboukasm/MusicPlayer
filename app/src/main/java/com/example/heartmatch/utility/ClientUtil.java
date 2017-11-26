package com.example.heartmatch.utility;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ClientUtil {

    private static OkHttpClient mClient;

    public static OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .readTimeout(10, TimeUnit.SECONDS)//Set Reading time
                    .writeTimeout(10, TimeUnit.SECONDS)//set writing time
                    .connectTimeout(10, TimeUnit.SECONDS)//set connecting time
                    .build();
        }
        return mClient;
    }

}
