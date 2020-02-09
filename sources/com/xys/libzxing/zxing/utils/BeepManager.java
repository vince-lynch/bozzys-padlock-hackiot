package com.xys.libzxing.zxing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import com.xys.libzxing.R;
import java.io.Closeable;
import java.io.IOException;

public class BeepManager implements OnCompletionListener, OnErrorListener, Closeable {
    private static final float BEEP_VOLUME = 0.1f;
    private static final String TAG = "BeepManager";
    private static final long VIBRATE_DURATION = 200;
    private final Activity activity;
    private MediaPlayer mediaPlayer = null;
    private boolean playBeep;
    private boolean vibrate;

    public BeepManager(Activity activity2) {
        this.activity = activity2;
        updatePrefs();
    }

    private static boolean shouldBeep(SharedPreferences sharedPreferences, Context context) {
        return ((AudioManager) context.getSystemService("audio")).getRingerMode() == 2;
    }

    private synchronized void updatePrefs() {
        this.playBeep = shouldBeep(PreferenceManager.getDefaultSharedPreferences(this.activity), this.activity);
        this.vibrate = true;
        if (this.playBeep && this.mediaPlayer == null) {
            this.activity.setVolumeControlStream(3);
            this.mediaPlayer = buildMediaPlayer(this.activity);
        }
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (this.playBeep && this.mediaPlayer != null) {
            this.mediaPlayer.start();
        }
        if (this.vibrate) {
            ((Vibrator) this.activity.getSystemService("vibrator")).vibrate(VIBRATE_DURATION);
        }
    }

    private MediaPlayer buildMediaPlayer(Context context) {
        AssetFileDescriptor openRawResourceFd;
        MediaPlayer mediaPlayer2 = new MediaPlayer();
        mediaPlayer2.setAudioStreamType(3);
        mediaPlayer2.setOnCompletionListener(this);
        mediaPlayer2.setOnErrorListener(this);
        try {
            openRawResourceFd = context.getResources().openRawResourceFd(R.raw.beep);
            mediaPlayer2.setDataSource(openRawResourceFd.getFileDescriptor(), openRawResourceFd.getStartOffset(), openRawResourceFd.getLength());
            openRawResourceFd.close();
            mediaPlayer2.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer2.prepare();
            return mediaPlayer2;
        } catch (IOException e) {
            Log.w(TAG, e);
            mediaPlayer2.release();
            return null;
        } catch (Throwable th) {
            openRawResourceFd.close();
            throw th;
        }
    }

    public void onCompletion(MediaPlayer mediaPlayer2) {
        mediaPlayer2.seekTo(0);
    }

    public synchronized boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
        if (i == 100) {
            try {
                this.activity.finish();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            mediaPlayer2.release();
            this.mediaPlayer = null;
            updatePrefs();
        }
        return true;
    }

    public synchronized void close() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
}
