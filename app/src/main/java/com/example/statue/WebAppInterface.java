package com.example.statue;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.IOException;

public class WebAppInterface {
    Context mContext;

    float mStatueFreq;
    float mStatueOverDelay;
    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c) {
        mContext = c;
        mStatueFreq = 1/2f;   // 30 minutes
        mStatueOverDelay = 1/2f; // 30 minute
    }

    WebAppInterface() {
        mStatueFreq = 1/2f;   // 30 minutes
        mStatueOverDelay = 1/2f; // 1 minute
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    public void setStatueFrequency(float frequency)
    {
        Log.d("DBG","setStatueFrequency:");
        Log.d("DBG", String.valueOf(frequency));
        mStatueFreq = frequency;
    }

    public long getStatueFrequency()
    {
        double timeinmillis = mStatueFreq * 60f * 60f * 1000f;
        Log.d("DBG","getStatueFrequency:");
        Log.d("DBG", String.valueOf(timeinmillis));
        return (long)timeinmillis;
        //return 3*60000;
    }

    public void setStatueOverDelay(float delay)
    {
        Log.d("DBG","setStatueOverDelay:");
        Log.d("DBG", String.valueOf(delay));
        mStatueOverDelay = delay;
    }

    public long getStatueOverDelay()
    {
        double timeinmillis = mStatueOverDelay * 60f * 1000f;
        Log.d("DBG","getStatueOverDelay:");
        Log.d("DBG", String.valueOf(timeinmillis));
        return (long)timeinmillis;
        //return 4000;
    }

    public void startStatue(long time)
    {

    }

    public void stopStatue()
    {

    }


}
