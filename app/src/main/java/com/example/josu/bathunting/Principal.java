package com.example.josu.bathunting;

import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class Principal extends ActionBarActivity {

    private Vista vista;
    //private static final int TOGGLE_SOUND = 1;
    private boolean soundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_principal_layout);
        vista = (Vista)findViewById(R.id.vista);
        vista.setKeepScreenOn(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sound) {
            String soundEnabledText = "Sound On";
            if (soundEnabled) {
                soundEnabled = false;
                soundEnabledText = "Sound Off";
            } else {
                soundEnabled = true;
            }
            Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }
}
