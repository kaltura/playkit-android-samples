package com.kaltura.playkit.samples.basicpluginssetup.plugins;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;
import com.kaltura.playkit.MessageBus;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKPlugin;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerDecorator;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.ads.AdController;

/**
 * This is a Sample custom plugin. Which is overriding the {@link PKPlugin}.
 *
 * @hide
 */

public class SamplePlugin extends PKPlugin {

    private static final String TAG = SamplePlugin.class.getSimpleName();


    private Player player; //Reference to player instance.
    private Context context; //Reference to android context instance
    private MessageBus messageBus; //Reference to message bus instance.

    private int value1; // custom value 1.
    private boolean value2; // custom value 2.

    //Create instance of the plugin factory.
    public static final Factory factory = new Factory() {

        //Set it to return the name of the plugin. We will use this name to register the plugin to the player.
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

    //Will be called when plugin is loaded.
    @Override
    protected void onLoad(Player player, Object config, final MessageBus messageBus, Context context) {

        Log.i(TAG, "Loading");

        //Keep reference to the player, context and message bus.
        // Probably you will need them.
        this.player = player;
        this.context = context;
        this.messageBus = messageBus;

        //Retrieve your custom values that were passed to plugin.
        value1 = ((JsonObject) config).getAsJsonPrimitive("value1").getAsInt();
        value2 = ((JsonObject) config).getAsJsonPrimitive("value1").getAsBoolean();

        //Just print that values to log.
        Log.d(TAG, "value1 = " + value1);
        Log.d(TAG, "value2 = " + value2);

        //Subscribe to player events.
        subscribeToPlayerEvents();
    }

    /**
     * Subscribe to the desired player events.
     */
    private void subscribeToPlayerEvents() {
        messageBus.listen(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {

                //Print received event name. Note, you will receive only events you subscribed to.
                Log.d(TAG, "PlayerEvent received: " + event.eventType().name());

                //Event ended received.
                if (event.eventType() == PlayerEvent.Type.ENDED) {
                    //Do whenever you need to do when this event received.

                } else if (event.eventType() == PlayerEvent.Type.PLAY) {
                    //Do whenever you need to do when this event received.

                }
                else if (event.eventType() == PlayerEvent.Type.PAUSE) {
                    //Do whenever you need to do when this event received.

                }
            }
            //Events to subscribe to.
        }, PlayerEvent.Type.PLAY, PlayerEvent.Type.PAUSE, PlayerEvent.Type.ENDED);
    }

    @Override
    protected void onUpdateMedia(PKMediaConfig mediaConfig) {
        Log.d(TAG, "onUpdateMedia");
    }

    @Override
    protected void onUpdateConfig(Object config) {
        Log.d(TAG, "onUpdateConfig");
    }

    @Override
    protected void onApplicationPaused() {
        Log.d(TAG, "onApplicationPaused");
    }

    @Override
    protected void onApplicationResumed() {
        Log.d(TAG, "onApplicationResumed");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }


    /**
     * Player decorator which allow more flexible access to the player workflow.
     * @return - the {@link PlayerDecorator} instance.
     */
    @Override
    protected PlayerDecorator getPlayerDecorator() {
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
