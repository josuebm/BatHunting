package com.example.josu.bathunting;

import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class Principal extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_layout);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
