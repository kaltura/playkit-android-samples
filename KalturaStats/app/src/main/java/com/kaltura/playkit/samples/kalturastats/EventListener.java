package com.kaltura.playkit.samples.kalturastats;

import com.kaltura.playkit.plugins.ovp.KalturaStatsEvent;

/**
 * Created by root on 12/18/17.
 */

public interface EventListener {
    void onReportEvent(KalturaStatsEvent.KalturaStatsReport kalturaStatsReport);
}
