package com.kaltura.playkit.samples.chromecastsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterOvpCast extends ConverterGoogleCast {

    private String ks;

    public ConverterOvpCast(ReceiverEnvironmentType receiverEnvironmentType, String ks, String entryId, String adTagURL, String mwEmbedURL, String partnerId, String uiconfId, ConverterMediaMetadata mediaMetadata) {
        super(receiverEnvironmentType, entryId, adTagURL, mwEmbedURL, partnerId, uiconfId, mediaMetadata);
        this.ks = ks;
    }

    public ConverterOvpCast(ConverterGoogleCast converterGoogleCast, String ks) {
        super(converterGoogleCast.getReceiverEnvironmentType(),
                converterGoogleCast.getEntryId(),
                converterGoogleCast.getAdTagURL(),
                converterGoogleCast.getMwEmbedURL(),
                converterGoogleCast.getPartnerId(),
                converterGoogleCast.getUiconfId(),
                converterGoogleCast.getMediaMetadata());
        this.ks = ks;
    }

    public String getKs() {
        return ks;
    }
}
