package com.kaltura.playkit.samples.mediasession;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;

/**
 * Created by gilad.nadav on 07/06/2017.
 */

public class RemoteControlReceiver extends android.support.v4.media.session.MediaButtonReceiver {

    private static final String TAG = "RemoteControlReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("XXX", "ZZZ");

        final String action = intent.getAction();
        Log.d("XXX", "action = " + action);
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        Log.d("XXX", "event = " + event.getKeyCode());

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                MainActivity.mMediaControllerCompat.getTransportControls().play();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                MainActivity.mMediaControllerCompat.getTransportControls().pause();
                break;
            case KEYCODE_MEDIA_NEXT:
                MainActivity.mMediaControllerCompat.getTransportControls().skipToNext();
                break;
            case KEYCODE_MEDIA_PREVIOUS:
                MainActivity.mMediaControllerCompat.getTransportControls().skipToPrevious();
                break;
        }

//            String intentAction = intent.getAction();
//                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
//                    KeyEvent event = (KeyEvent) intent
//                            .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//                    int action = event.getAction();
//                    Log.d("XXX", "action = " + action);
//
//                    setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
//
//                    String command = null;
//                    switch (keycode) {
//                        case KeyEvent.KEYCODE_MEDIA_STOP:
//                            command = "CMDSTOP";
//                            break;
//                        case KeyEvent.KEYCODE_HEADSETHOOK:
//                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                            command = "CMDTOGGLEPAUSE";
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_NEXT:
//                            command = "CMDNEXT";
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                            command = "CMDPREVIOUS";
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                            command = "CMDPAUSE";
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_PLAY:
//                            command = "CMDPLAY";
//                            break;
//                    }
//                //player.pause();
//            }
    }
}
