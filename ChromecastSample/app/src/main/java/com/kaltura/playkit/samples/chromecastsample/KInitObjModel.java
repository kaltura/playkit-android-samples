package com.kaltura.playkit.samples.chromecastsample;

import com.google.gson.JsonObject;

public class KInitObjModel {

    private KLocaleModel mLocale;
    private String mPlatform;
    private String mSiteGuid;
    private int mDomainID;
    private String mUDID;
    private String mApiUser;
    private String mApiPass;

    public KInitObjModel() {
        mLocale = new KLocaleModel();
    }

    public JsonObject toJson() {
        JsonObject initObj = new JsonObject();
        JsonObject obj = new JsonObject();
            obj.addProperty("ApiPass", mApiPass);
            obj.addProperty("ApiUser", mApiUser);
            obj.addProperty("UDID", mUDID);
            obj.addProperty("DomainID", mDomainID);
            obj.addProperty("SiteGuid", mSiteGuid);
            obj.addProperty("Platform", mPlatform);
            obj.add("Locale", mLocale.toJson());
            initObj.add("initObj", obj);

        return initObj;
    }

    public void setLocale(KLocaleModel locale) {
        mLocale = locale;
    }

    public void setPlatform(String platform) {
        mPlatform = platform;
    }

    public void setSiteGuid(String siteGuid) {
        mSiteGuid = siteGuid;
    }

    public void setDomainID(int domainID) {
        mDomainID = domainID;
    }

    public void setUDID(String UDID) {
        mUDID = UDID;
    }

    public void setApiUser(String apiUser) {
        mApiUser = apiUser;
    }

    public void setApiPass(String apiPass) {
        mApiPass = apiPass;
    }
}