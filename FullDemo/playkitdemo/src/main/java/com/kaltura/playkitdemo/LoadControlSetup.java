package com.kaltura.playkitdemo;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.player.LoadControlBuffers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoadControlSetup {
    private static final String TAG = "LoadControlSetup";

    private static final String GET_UICONF_URL = "https://cdnapisec.kaltura.com/api_v3/?format=1&partnerId=1982551&service=uiconf&action=get&id=";
    private static final String JSON_FILE_NAME = "LoadControlSetup.json";

    private static boolean loaded = false;
    private static File configFile;
    private static Config config;

    // Gson and JsonParser are reusable
    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new Gson();

    // Call once as soon as the app loads, in Application.onCreate()
    @SuppressWarnings("WeakerAccess")
    public static void load(Context context, int uiConfId) {
        if (loaded) {
            return;
        }

        configFile = new File(context.getFilesDir(), JSON_FILE_NAME);

        // Read from file for immediate use. Later update the file.
        readFromFile();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GET_UICONF_URL + uiConfId).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "failure Response: " + e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (loaded) {
                    return;
                }

                if (response.isSuccessful()) {
                    final ResponseBody body = response.body();
                    if (body != null) {
                        extractConfigFromUIConf(body.string());
                        loaded = true;
                    }
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public static String getConfigurationId() {
        return config == null ? null : config.configurationId;
    }

    private static void extractConfigFromUIConf(String string) {
        JsonObject uiconf = parser.parse(string).getAsJsonObject();
        if (uiconf != null) {
            JsonPrimitive jsonPrimitive = uiconf.getAsJsonPrimitive("config");
            if (jsonPrimitive != null) {
                final String configJson = jsonPrimitive.getAsString();
                config = gson.fromJson(configJson, Config.class);
                if (config == null) {
                    return;
                }

                // Write to file as a clean json
                saveToFile(gson.toJson(config));
            }
        }
    }

    private static void saveToFile(String json) {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.append(json);
        } catch (IOException e) {
            Log.e(TAG, "saveToFile: failed", e);
        }
    }

    private static void readFromFile() {

        try (FileReader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, Config.class);

        } catch (IOException e) {
            Log.e(TAG, "readFromFile: failed", e);
        }
    }

    // Call after creating a player with loadPlayer()
    @SuppressWarnings("WeakerAccess")
    public static void apply(Player player) {
        if (config == null) {
            return;
        }

        player.getSettings().setPlayerBuffers(
                new LoadControlBuffers()
                        .setMinPlayerBufferMs(config.initialBuffer)
                        .setMaxPlayerBufferMs(config.bufferLength)
                        .setMinBufferAfterReBufferMs(config.afterRebuffer));
    }

    @SuppressWarnings("unused") // assigned by Gson
    private class Config {
        private String configurationId;
        private Integer initialBuffer;
        private Integer afterRebuffer;
        private Integer bufferLength;
    }
}
