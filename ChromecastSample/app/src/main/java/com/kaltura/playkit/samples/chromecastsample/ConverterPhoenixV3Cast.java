package com.kaltura.playkit.samples.chromecastsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterPhoenixV3Cast {
    String entryId;
    String mediaType;
    String assetReferenceType;
    String contextType;
    String format;
    String fileId;
    String protocol;
    String ks;
    String vmapAdTagUrl;
    String vastAdTagUrl;



    public ConverterPhoenixV3Cast(String entryId, String mediaType,
    String assetReferenceType,
    String contextType,
    String format,
    String fileId,
    String protocol,
    String ks,
    String vmapAdTagUrl,
    String vastAdTagUrl) {
        this.entryId = entryId;
        this.mediaType = mediaType;
        this.assetReferenceType = assetReferenceType;
        this.contextType = contextType;
        this.format = format;
        this.fileId = fileId;
        this.protocol = protocol;
        this.ks = ks;
        this.vmapAdTagUrl = vmapAdTagUrl;
        this.vastAdTagUrl = vastAdTagUrl;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getVmapAdTagUrl() {
        return vmapAdTagUrl;
    }

    public String getVastAdTagUrl() {
        return vastAdTagUrl;
    }

    public String getKs() {
        return ks;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getAssetReferenceType() {
        return assetReferenceType;
    }

    public String getContextType() {
        return contextType;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFormat() {
        return format;
    }

}
