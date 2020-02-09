package android.support.v4.media;

import android.os.SystemClock;
import android.view.KeyEvent;

@Deprecated
public abstract class TransportPerformer {
    static final int AUDIOFOCUS_GAIN = 1;
    static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
    static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
    static final int AUDIOFOCUS_LOSS = -1;
    static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
    static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;

    @Deprecated
    public int onGetBufferPercentage() {
        return 100;
    }

    @Deprecated
    public abstract long onGetCurrentPosition();

    @Deprecated
    public abstract long onGetDuration();

    @Deprecated
    public int onGetTransportControlFlags() {
        return 60;
    }

    @Deprecated
    public abstract boolean onIsPlaying();

    @Deprecated
    public boolean onMediaButtonUp(int i, KeyEvent keyEvent) {
        return true;
    }

    @Deprecated
    public abstract void onPause();

    @Deprecated
    public abstract void onSeekTo(long j);

    @Deprecated
    public abstract void onStart();

    @Deprecated
    public abstract void onStop();

    @Deprecated
    public boolean onMediaButtonDown(int i, KeyEvent keyEvent) {
        switch (i) {
            case 79:
            case 85:
                if (!onIsPlaying()) {
                    onStart();
                    break;
                } else {
                    onPause();
                    break;
                }
            case 86:
                onStop();
                return true;
            case TransportMediator.KEYCODE_MEDIA_PLAY /*126*/:
                onStart();
                return true;
            case TransportMediator.KEYCODE_MEDIA_PAUSE /*127*/:
                onPause();
                return true;
        }
        return true;
    }

    @Deprecated
    public void onAudioFocusChange(int i) {
        int i2 = i != -1 ? 0 : TransportMediator.KEYCODE_MEDIA_PAUSE;
        if (i2 != 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = uptimeMillis;
            long j2 = uptimeMillis;
            int i3 = i2;
            KeyEvent keyEvent = new KeyEvent(j, j2, 0, i3, 0);
            onMediaButtonDown(i2, keyEvent);
            KeyEvent keyEvent2 = new KeyEvent(j, j2, 1, i3, 0);
            onMediaButtonUp(i2, keyEvent2);
        }
    }
}
