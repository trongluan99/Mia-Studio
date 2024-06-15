package com.mia.module;

import com.ads.mia.admob.Admob;
import com.ads.mia.admob.AppOpenManager;
import com.ads.mia.ads.MiaAd;
import com.ads.mia.application.AdsMultiDexApplication;
import com.ads.mia.config.AdjustConfig;
import com.ads.mia.config.MiaAdConfig;

public class App extends AdsMultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initAds();
    }

    private void initAds() {
        String environment = BuildConfig.DEBUG ? MiaAdConfig.ENVIRONMENT_DEVELOP : MiaAdConfig.ENVIRONMENT_PRODUCTION;
        mMiaAdConfig = new MiaAdConfig(this, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true, getResources().getString(R.string.adjust_token));
        mMiaAdConfig.setAdjustConfig(adjustConfig);
        mMiaAdConfig.setFacebookClientToken(getResources().getString(R.string.facebook_client_token));

        mMiaAdConfig.setIdAdResume("");

        MiaAd.getInstance().init(this, mMiaAdConfig);
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity.class);
    }
}
