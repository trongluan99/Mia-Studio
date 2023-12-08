package com.mia.module;

import com.ads.mia.ads.MiaAd;
import com.ads.mia.config.AdjustConfig;
import com.ads.mia.config.AppsflyerConfig;
import com.ads.mia.config.MiaAdConfig;
import com.ads.mia.application.AdsMultiDexApplication;
import com.ads.mia.applovin.AppLovin;
import com.ads.mia.applovin.AppOpenMax;
import com.ads.mia.billing.AppPurchase;
import com.ads.mia.admob.Admob;
import com.ads.mia.admob.AppOpenManager;
import com.mia.module.activity.MainActivity;
import com.mia.module.activity.SplashActivity;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends AdsMultiDexApplication {
    private final String APPSFLYER_TOKEN = "";
    private final String ADJUST_TOKEN = "";
    private final String EVENT_PURCHASE_ADJUST = "";
    private final String EVENT_AD_IMPRESSION_ADJUST = "";
    protected StorageCommon storageCommon;
    private static MyApplication context;
    public static MyApplication getApplication() {
        return context;
    }
    public StorageCommon getStorageCommon() {
        return storageCommon;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Admob.getInstance().setNumToShowAds(0);

        storageCommon = new StorageCommon();
        initBilling();
        initAds();

    }

    private void initAds() {
        String environment = BuildConfig.env_dev ? MiaAdConfig.ENVIRONMENT_DEVELOP : MiaAdConfig.ENVIRONMENT_PRODUCTION;
        miaAdConfig = new MiaAdConfig(this, MiaAdConfig.PROVIDER_ADMOB, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true,ADJUST_TOKEN);
        adjustConfig.setEventAdImpression(EVENT_AD_IMPRESSION_ADJUST);

        adjustConfig.setEventNamePurchase(EVENT_PURCHASE_ADJUST);
        miaAdConfig.setAdjustConfig(adjustConfig);

        AppsflyerConfig appsflyerConfig = new AppsflyerConfig(true,APPSFLYER_TOKEN);


        listTestDevice.add("EC25F576DA9B6CE74778B268CB87E431");
        miaAdConfig.setListDeviceTest(listTestDevice);
        miaAdConfig.setIntervalInterstitialAd(15);
        miaAdConfig.setAdjustTokenTiktok("123456");

        MiaAd.getInstance().init(this, miaAdConfig, false);

        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        AppLovin.getInstance().setDisableAdResumeWhenClickAds(true);
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);

        if (MiaAd.getInstance().getMediationProvider() == MiaAdConfig.PROVIDER_ADMOB) {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        } else {
            AppOpenMax.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        }
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(), listINAPId, listSubsId);
    }

}
