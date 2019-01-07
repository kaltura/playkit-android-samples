package com.kaltura.playkitdemo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.player.LoadControlBuffers;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class LoadControlSetup {
    public static final String TAG = "LoadControlSetup";

    public static final String GET_UICONF_DATA = "https://cdnapisec.kaltura.com/api_v3/?format=1&partnerId=1982551&service=uiconf&action=get&id=43565151";

    private static Integer experimentId;

    private static Integer initialBuffer;
    private static Integer afterRebuffer;
    private static Integer bufferLength;
    //private static int startBitrate;

    private static boolean loaded = false;

    // Call once as soon as the app loads, in Application.onCreate()
    public static void load() {
        if (loaded) {
            return;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GET_UICONF_DATA).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.e(TAG, "failure Response: " + mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    populateLoadControlsMembers(response);
                }
            }
        });
    }

    public static Integer getExperimentId() {
        return experimentId;
    }

    private static void populateLoadControlsMembers(Response response) throws IOException {
        if (response != null && response.body() != null) {
            String uiconfFile = response.body().string();
            JsonParser parser = new JsonParser();
            JsonObject uiconf = parser.parse(uiconfFile).getAsJsonObject();
            if (uiconf != null) {
                JsonPrimitive config = uiconf.getAsJsonPrimitive("config");
                if (config != null) {
                    JsonObject configJsonObj = parser.parse(config.getAsString()).getAsJsonObject();
                    if (configJsonObj != null) {
                        Gson gson = new Gson();
                        LoadControlModel loadControlModel = gson.fromJson(configJsonObj, LoadControlModel.class);
                        if (loadControlModel != null) {
                            initialBuffer = loadControlModel.getInitialBuffer();
                            afterRebuffer = loadControlModel.getAfterRebuffer();
                            bufferLength = loadControlModel.getBufferLength();
                            experimentId = loadControlModel.getExperimentId();
                        }
                    }
                }
            }
        }
    }

    // Call after creating a player with loadPlayer()
    public static void apply(Player player) {
        player.getSettings().setPlayerBuffers(
                new LoadControlBuffers().setMinPlayerBufferMs(LoadControlSetup.initialBuffer).
                        setMaxPlayerBufferMs(LoadControlSetup.bufferLength).
                        setMinBufferAfterReBufferMs(LoadControlSetup.afterRebuffer));
    }

    class LoadControlModel {

        private Integer experimentId;
        private Integer initialBuffer;
        private Integer afterRebuffer;
        private Integer bufferLength;
        private Integer startBitrate;

        public LoadControlModel() {}

        public Integer getExperimentId() {
            return experimentId;
        }

        public Integer getInitialBuffer() {
            return initialBuffer;
        }

        public Integer getAfterRebuffer() {
            return afterRebuffer;
        }

        public Integer getBufferLength() {
            return bufferLength;
        }

        public Integer getStartBitrate() {
            return startBitrate;
        }
    }
}