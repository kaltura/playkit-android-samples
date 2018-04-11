package com.kaltura.playkit.samples.chromecastsample;

import com.google.gson.JsonObject;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterOttCast extends ConverterGoogleCast {

    JsonObject initObject;
    String format;

    public ConverterOttCast(ReceiverEnvironmentType receiverEnvironmentType, JsonObject initObject, String format, String entryId, String adTagURL, String mwEmbedURL, String partnerId, String uiconfId, ConverterMediaMetadata mediaMetadata) {
        super(receiverEnvironmentType, entryId, adTagURL, mwEmbedURL, partnerId, uiconfId, mediaMetadata);
        this.format = format;
        this.initObject = initObject;
    }

    public ConverterOttCast(ConverterGoogleCast converterGoogleCast, String format, JsonObject initObject) {
        super(converterGoogleCast.getReceiverEnvironmentType(),
                converterGoogleCast.getEntryId(),
                converterGoogleCast.getAdTagURL(),
                converterGoogleCast.getMwEmbedURL(),
                converterGoogleCast.getPartnerId(),
                converterGoogleCast.getUiconfId(),
                converterGoogleCast.getMediaMetadata());
        this.format = format;
        this.initObject = initObject;
    }


    public String getFormat() {
        return format;
    }

    public JsonObject getInitObject() {
        return initObject;
    }
}
