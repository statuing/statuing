package com.example.statue;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends Activity {

    long mStartTime = 0;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private Random mRandom = new Random();
    private WebAppInterface mSettings = new WebAppInterface();
    private int mSongIndex = 0;
    private SoundPool mSoundPool = null;
    private static int mStatue = 0;
    private static int mBell = 0;
    private long mPrevOffset;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler mTimerHandler = new Handler();
    Runnable mTimerRunnable = () -> {
        /*long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;*/

        Log.d("DBG","Runnable:Run()");
        Log.d("DBG", String.valueOf(mSongIndex));

        mSongIndex++;

        if (mSongIndex > 1)
        {
            mSongIndex = 0;
            prepareStatueOver();
        }
        else
        {
            prepareStatue();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrevOffset = 0;

        // add a webview with id @+id/the_webwiev to your main.xml layout file
        /*WebView wv = (WebView)findViewById(R.id.statuing_webview);
        wv.loadUrl("file:////android_asset/www/html/index.html");

        wv.requestFocus();
        wv.getSettings().setLightTouchEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebAppInterface(this), "Android");*/

        ToggleButton toggle = findViewById(R.id.toggle);

        // initialize SoundPool, load sounds
        prepare_sound_play("none");

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled
                //mStartTime = System.currentTimeMillis();
                mSongIndex = 0;
                mStartTime = randomWaitInterval(mSettings.getStatueFrequency());
                mPrevOffset = mSettings.getStatueFrequency() - mStartTime;
                if(mTimerHandler.postDelayed(mTimerRunnable, mStartTime))
                {
                    Log.d("DBG", String.valueOf(mStartTime));
                }

                Log.d("DBG","onCheckedChanged:ON:");
            } else {
                // The toggle is disabled
                mTimerHandler.removeCallbacks(mTimerRunnable);
            }
        });

    }

    public void refresh()
    {
        ToggleButton toggle = findViewById(R.id.toggle);
        if (toggle.isChecked()) {
            mTimerHandler.removeCallbacks(mTimerRunnable);
            toggle.setChecked(true);
        }
    }

    public void onStatueFreqChanged(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        int resID = view.getId();
        // Check which radio button was clicked
        if(resID == R.id.thirtyminutes) {
            if (checked)
                mSettings.setStatueFrequency(1 / 2f);
        }else if(resID == R.id.onehour) {
            if (checked)
                mSettings.setStatueFrequency(1f);
        } else if(resID == R.id.twohours) {
            if (checked)
                mSettings.setStatueFrequency(2f);
        } else if(resID == R.id.threehours) {
            if (checked)
                mSettings.setStatueFrequency(3f);
        } else if(resID == R.id.sixhours) {
            if (checked)
                mSettings.setStatueFrequency(6f);
        } else if(resID == R.id.twelvehours) {
            if (checked)
                mSettings.setStatueFrequency(12f);
        } else if(resID == R.id.eighteenhours) {
            if (checked)
                mSettings.setStatueFrequency(18f);
        } else if(resID == R.id.aday) {
            if (checked)
                mSettings.setStatueFrequency(24f);
        }

        //((RadioButton) view).setChecked(true);

        //mStartTime = randomWaitInterval(mSettings.getStatueFrequency());
        //mTimerHandler.postDelayed(mTimerRunnable, mStartTime);
        Log.d("DBG","onStatueFreqChanged:");
        //Log.d("DBG", String.valueOf(mStartTime));
        //refresh();

    }

    public void onStatueOverTimeChanged(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        int resID = view.getId();
        // Check which radio button was clicked
        if (resID == R.id.halfminute) {
            if (checked)
                mSettings.setStatueOverDelay(1 / 2f);
        }
        else if(resID == R.id.oneminute) {
            if (checked)
                mSettings.setStatueOverDelay(1f);
        }
        else if (resID == R.id.twominutes)
        {
            if (checked)
                mSettings.setStatueOverDelay(2f);
        }

        //((RadioButton) view).setChecked(true);

        //mStartTime = randomWaitInterval(mSettings.getStatueOverDelay());
        //mTimerHandler.postDelayed(mTimerRunnable, mStartTime);
        Log.d("DBG","onStatueOverTimeChanged:");
        //Log.d("DBG", String.valueOf(mStartTime));
        //refresh();
    }

    public long randomWaitInterval(long intervalrangemillis)
    {
        long randomizedNum;

        randomizedNum = (long)(mRandom.nextDouble()*(double) intervalrangemillis);
        Log.d("DBG","randomWaitInterval:");
        Log.d("DBG", String.valueOf(intervalrangemillis));
        Log.d("DBG", String.valueOf(randomizedNum));
        return randomizedNum;
    }

    public void onPause() {
        super.onPause();
        //mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    public void prepareStatue()
    {
        String path = "file:///android_asset/music";
        String fileName = "statue";
        //prepare_play(path, fileName);
        prepare_sound_play(fileName);
    }

    public void prepareStatueOver()
    {
        String path = "file:///android_asset/music";
        String fileName = "bell";
        //prepare_play(path, fileName);
        prepare_sound_play(fileName);
    }

    public void prepare_play(String path, String filename)
    {
        MediaPlayer mediaPlayer;

        try {
            String fn = "android.resource://" + this.getPackageName() + "/raw/" + filename;

            if (filename.equals("statue")) {
                mediaPlayer = MediaPlayer.create(this, R.raw.statue);
            }
            else {
                mediaPlayer = MediaPlayer.create(this, R.raw.bell);
            }

            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }

            mediaPlayer.release();

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void prepare_sound_play(String filename)
    {
        if (mSoundPool == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                AudioAttributes audioAttributes = new AudioAttributes
                        .Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                mSoundPool = new SoundPool
                        .Builder()
                        .setMaxStreams(2)
                        .setAudioAttributes(audioAttributes)
                        .build();
            }
            else {
                mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            }

            mSoundPool.setOnLoadCompleteListener ((soundPool, sampleId, status) -> {

                if (status == 0 && mStatue == sampleId)
                {
                    Log.d("DBG","onLoadComplete(statue):");
                    soundPool.play(mStatue, 1, 1, 0, 0, 1);

                    soundPool.unload(mStatue);

                    mStatue = 0;

                    mStartTime = randomWaitInterval(mSettings.getStatueOverDelay()); // 30*1000;
                }
                else if (status == 0 && mBell == sampleId)
                {
                    Log.d("DBG","onLoaComplete(bell):");
                    soundPool.play(mBell, 1, 1, 0, 0, 1);

                    soundPool.unload(mBell);

                    mBell = 0;

                    mStartTime = randomWaitInterval(mSettings.getStatueFrequency());// 3*60*1000;
                    long nextOffset = mSettings.getStatueFrequency() - mStartTime;
                    mStartTime += mPrevOffset;
                    mPrevOffset = nextOffset;
                }

                if (mTimerHandler.postDelayed(mTimerRunnable, mStartTime))
                {
                    Log.d("DBG", String.valueOf(mStartTime));
                }
            });

        }

        if (mSoundPool != null)
        {
            if (filename.equals("statue"))
            {
                // This load function takes
                // three parameter context,
                // file_name and priority.
                mStatue = mSoundPool.load(this, R.raw.statue, 1);
            }
            else if (filename.equals("bell"))
            {
                mBell = mSoundPool.load(this, R.raw.bell, 1);
            }
        }

    }

}