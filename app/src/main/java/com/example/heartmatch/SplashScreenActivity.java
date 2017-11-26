package com.example.heartmatch;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.musicplayer.R;

public class SplashScreenActivity extends AppCompatActivity {

    // variable that determines length of the splash screen
    private int SLEEP_TIMER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();
    }

    private class LogoLauncher extends Thread{
        public void run() {
            try{
                sleep(1000 * SLEEP_TIMER);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }

            // switch main activity to first screen of our application
            Intent intent = new Intent(SplashScreenActivity.this, MusicActivity.class);
            startActivity(intent);
            SplashScreenActivity.this.finish();
        }

    }
}
