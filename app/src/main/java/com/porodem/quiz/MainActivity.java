package com.porodem.quiz;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    int soundAganim;

    public static final String LOG = "myLogs";

    SoundPool sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add Sound Pool
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        soundAganim = sp.load(this, R.raw.aganim_venom, 1);

     //test for compare two arrays
        String[] res = new String[4];
        res[0]="cow";
        res[1]="bat";
        res[2]="pig";
        res[3]="pig";
        String x = Arrays.toString(res);
        Log.d(LOG, "-- res: " + x);

        String[] res2 = new String[4];
        res2[0]="pig";
        res2[1]="pig";
        res2[2]="bat";
        res2[3]="cow";
        String y = Arrays.toString(res2);
        Log.d(LOG, "-- res2: " + y);

        if (res.length != res2.length) {
            Log.d(LOG, "-- not equal length");
        } else {
            Log.d(LOG, "-- equal length");
        }
        int on = 0;
        int i;
        int j;
        for (i = 0; i < res.length; i++) {
            for (j = 0; j < res2.length; j++) {
                if (res[i] == res2[j]){
                    on++;
                }
            }
        }
        if (on==res.length) {
            Log.d(LOG, "-- Array equal--");
        } else {
            Log.d(LOG, "-- Not equal array --");
        }

    }

    public void click_start(View view) {
        //play sound
        //sp.play(soundAganim,1,1,0,0,1);
        Intent intent = new Intent(MainActivity.this, Quiz.class);
        startActivity(intent);

    }


    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d(LOG, "onLoadComplete, sampleId = " + sampleId + ", status = " + status);
    }
}
