package com.waveformexample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.waveform.WaveformView;

public class MainActivity extends AppCompatActivity {

    private WaveformView waveformView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveformView = (WaveformView) findViewById(R.id.waveform);
        waveformView.setWaveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    protected void onStart() {
        super.onStart();
        waveformView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        waveformView.stop();
    }
}
