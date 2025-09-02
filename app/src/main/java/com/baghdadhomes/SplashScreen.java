package com.baghdadhomes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.baghdadhomes.Activities.AllPropertiesActivity;

public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Thread timer = new Thread() {
            public void run() {
                try {
                    //Display for 3 seconds
                    sleep(3000);
                } catch (InterruptedException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } finally {
                    Intent openstartingpoint = new Intent(SplashScreen.this, AllPropertiesActivity.class);
                    finish();
                    startActivity(openstartingpoint);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
