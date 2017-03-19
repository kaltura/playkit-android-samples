package com.kaltura.playkit.samples.basicpluginssetup.plugins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.kaltura.playkit.MessageBus;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKPlugin;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerDecorator;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.ads.AdController;

/**
 * @hide
 */

public class SamplePlugin extends PKPlugin {

    private static final PKLog log = PKLog.get("SamplePlugin");

    private Player player;
    private Context context;
    private MessageBus messageBus;

    private int value1;
    private boolean value2;

    public static final Factory factory = new Factory() {
        @Override
        public String getName() {
            return "Sample";
        }

        @Override
        public PKPlugin newInstance() {
            return new SamplePlugin();
        }

        @Override
        public void warmUp(Context context) {
            
        }
    };

    @Override
    protected void onLoad(Player player, Object config, final MessageBus messageBus, Context context) {
        log.i("Loading");
        this.player = player;
        this.context = context;
        this.messageBus = messageBus;
        value1 = ((JsonObject) config).getAsJsonPrimitive("value1").getAsInt();
        value2 = ((JsonObject) config).getAsJsonPrimitive("value1").getAsBoolean();
        log.d("value1 = " + value1);
        log.d("value2 = " + value2);

        this.messageBus.listen(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("onLoad:PlayerEvent:" + event);
                if (event.eventType() == PlayerEvent.Type.ENDED) {
                    log.d("Content Completed");
                } else if (event.eventType() == PlayerEvent.Type.PLAY) {
                    log.d("PLAY Selected");
                }
                else if (event.eventType() == PlayerEvent.Type.PAUSE) {
                    log.d("PAUSE Selected");
                }
            }
        }, PlayerEvent.Type.PLAY, PlayerEvent.Type.PAUSE, PlayerEvent.Type.ENDED);
    }

    @Override
    protected void onUpdateMedia(PKMediaConfig mediaConfig) {
        log.d("onUpdateMedia");
    }

    @Override
    protected void onUpdateConfig(Object config) {
        log.d("onUpdateConfig");
    }

    @Override
    protected void onApplicationPaused() {
        log.d("onApplicationPaused");
    }

    @Override
    protected void onApplicationResumed() {
        log.d("onApplicationResumed");
    }

    @Override
    public void onDestroy() {
        log.d("onDestroy");
    }

    @Override
    protected PlayerDecorator getPlayerDecorator() {
        // Can be implemented separately and implement the logic when to call decorator method or player method
        return new PlayerDecorator() {
            @Override
            public void prepare(@NonNull PKMediaConfig mediaConfig) {
                super.prepare(mediaConfig);
            }

            @Override
            public long getDuration() {
                return super.getDuration();
            }

            @Override
            public long getCurrentPosition() {
                return super.getCurrentPosition();
            }

            @Override
            public void seekTo(long position) {
                super.seekTo(position);
            }

            @Override
            public AdController getAdController() {
                return super.getAdController();
            }

            @Override
            public void play() {
                super.play();
            }

            @Override
            public void pause() {
                super.pause();
            }

            @Override
            public void replay() {
                super.replay();
            }

            @Override
            public void setVolume(float volume) {
                super.setVolume(volume);
            }

            @Override
            public boolean isPlaying() {
                return super.isPlaying();
            }

            @Override
            public void prepareNext(@NonNull PKMediaConfig mediaConfig) {
                super.prepareNext(mediaConfig);
            }

            @Override
            public long getBufferedPosition() {
                return super.getBufferedPosition();
            }

            @Override
            public void changeTrack(String uniqueId) {
                super.changeTrack(uniqueId);
            }
        };
    }
}
