package com.kaltura.playkit.samples.youbora;

import com.kaltura.playkit.plugins.youbora.YouboraEvent;

/**
 * Created by root on 12/15/17.
 */

public interface EventListener {
    void onYouboraEvent(YouboraEvent.YouboraReport youboraReport);
}
