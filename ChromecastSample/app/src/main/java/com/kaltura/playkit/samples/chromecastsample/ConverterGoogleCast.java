package com.kaltura.playkit.samples.chromecastsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterGoogleCast extends ConverterAddon {


    public enum ReceiverEnvironmentType {

        RECEIVER_TVPAPI_ENVIRONMENT,
        RECEIVER_OVP_ENVIRONMENT;

    }

    ReceiverEnvironmentType receiverEnvironmentType;
    String entryId;
    String adTagURL;
    String mwEmbedURL;
    String partnerId;
    String uiconfId;
    ConverterMediaMetadata mediaMetadata;


    public ConverterGoogleCast(ReceiverEnvironmentType receiverEnvironmentType, String entryId, String adTagURL, String mwEmbedURL, String partnerId, String uiconfId, ConverterMediaMetadata mediaMetadata) {
        this.receiverEnvironmentType = receiverEnvironmentType;
        this.entryId = entryId;
        this.adTagURL = adTagURL;
        this.mwEmbedURL = mwEmbedURL;
        this.partnerId = partnerId;
        this.uiconfId = uiconfId;
        this.mediaMetadata = mediaMetadata;
    }

    public ReceiverEnvironmentType getReceiverEnvironmentType() {
        return receiverEnvironmentType;
    }

    public void setReceiverEnvironmentType(ReceiverEnvironmentType receiverEnvironmentType) {
        this.receiverEnvironmentType = receiverEnvironmentType;
    }

    public String getAdTagURL() {
        return adTagURL;
    }

    public String getEntryId() {
        return entryId;
    }

    public ConverterMediaMetadata getMediaMetadata() {
        return mediaMetadata;
    }

    public String getMwEmbedURL() {
        return mwEmbedURL;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getUiconfId() {
        return uiconfId;
    }

}
