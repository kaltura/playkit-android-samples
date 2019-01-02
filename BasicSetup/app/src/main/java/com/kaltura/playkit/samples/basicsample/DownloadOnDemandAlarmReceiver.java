package com.kaltura.playkit.samples.basicsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadOnDemandAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DownloadOnDemandService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}