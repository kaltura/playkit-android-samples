package com.kaltura.playkit.samples.chromecastcafsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterOvpV3Cast {
    String entryId;
    String ks;
    String vmapAdTagUrl;
    String vastAdTagUrl;

    public ConverterOvpV3Cast(String entryId,
                              String ks,
                              String vmapAdTagUrl,
                              String vastAdTagUrl) {
        this.entryId = entryId;
        this.ks = ks;
        this.vmapAdTagUrl = vmapAdTagUrl;
        this.vastAdTagUrl = vastAdTagUrl;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getKs() {
        return ks;
    }

    public String getVmapAdTagUrl() {
        return vmapAdTagUrl;
    }

    public String getVastAdTagUrl() {
        return vastAdTagUrl;
    }
}
