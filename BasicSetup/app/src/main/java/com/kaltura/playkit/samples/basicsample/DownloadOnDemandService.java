package com.kaltura.playkit.samples.basicsample;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadOnDemandService extends IntentService {
    private static final String TAG = DownloadOnDemandService.class.getSimpleName();

    public DownloadOnDemandService() {
        super("XXXXXXXXXXX DownloadOnDemandService constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.d(TAG, "XXXXXXXXXXX DownloadOnDemandService Wakeup");
    }
}