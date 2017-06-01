package com.kaltura.playkit.samples.androidtv;

import com.google.gson.JsonObject;

/**
 * Created by glenford on 13/12/16.
 */

public abstract class ConverterPlugin {

    String pluginName;

    public abstract JsonObject toJson();


    public String getPluginName() {
        return pluginName;
    }


    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

}
