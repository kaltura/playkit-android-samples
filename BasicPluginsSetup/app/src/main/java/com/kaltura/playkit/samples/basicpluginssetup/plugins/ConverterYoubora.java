package com.kaltura.playkit.samples.basicpluginssetup.plugins;

import com.google.gson.JsonObject;

public class ConverterYoubora extends ConverterPlugin {
    JsonObject youboraConfig;
    JsonObject ads;
    JsonObject properties;
    JsonObject extraParams;

    public ConverterYoubora(JsonObject youboraConfig, JsonObject ads, JsonObject extraParams, JsonObject properties) {
        this.youboraConfig = youboraConfig;
        this.ads = ads;
        this.extraParams = extraParams;
        this.properties = properties;
    }


    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("youboraConfig", youboraConfig);
        jsonObject.add("ads", ads);
        jsonObject.add("properties", properties);
        jsonObject.add("extraParams", extraParams);
        return jsonObject;
    }
}
