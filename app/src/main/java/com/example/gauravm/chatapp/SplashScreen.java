package com.example.gauravm.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

//import android.util.Log;

public class SplashScreen extends Activity {
    ProgressBar bar;
    TextView txt;
    int total = 0;
    boolean isRunning = false;

    // handler for the background updating
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            total = total + 5;
            String perc = String.valueOf(total).toString();
            txt.setText("Loading..."+ perc + " %");
            TextView nameverval=(TextView ) findViewById(R.id.namever);
            Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
            nameverval.setTypeface(light);
            nameverval.setText(getString(R.string.app_name) + " V " +getVersion());

            if(perc.equalsIgnoreCase("100"))
            {
                Intent intent = new Intent(SplashScreen.this,Register.class);
                startActivity(intent);
                SplashScreen.this.finish();
            }
            bar.incrementProgressBy(5);
        }

    };


    private String getVersion() {
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            version = pInfo.versionName;
        } catch (NameNotFoundException e1) {
            Log.e(this.getClass().getSimpleName(), "Name not found", e1);
        }
        return version;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        bar =  findViewById(R.id.ProgressBar01);
        txt = findViewById(R.id.txtrere);


    }

    public void onStart() {
        super.onStart();
// reset the bar to the default value of 0
        bar.setProgress(0);
// create a thread for updating the progress bar
        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 20 && isRunning; i++) {
// wait 1000ms between each update
                        handler.sendEmptyMessage(0);
                        Thread.sleep(200);


                    }
                }
                catch (Throwable t) {              }            }       });
        isRunning = true;
// start the background thread
        background.start();
    }
    public void onStop() {        super.onStop();
    }}