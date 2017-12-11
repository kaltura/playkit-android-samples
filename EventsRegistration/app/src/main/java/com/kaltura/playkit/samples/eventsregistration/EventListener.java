package com.kaltura.playkit.samples.eventsregistration;

import com.kaltura.playkit.PlayerEvent;

/**
 * Created by root on 12/8/17.
 */

public interface EventListener {
    void onPlayerInit();
    void onPlayerStart(PlayerEvent event);
}
