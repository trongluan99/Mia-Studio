package com.mia.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.mia.admob.Admob;
import com.ads.mia.admob.AppOpenManager;
import com.ads.mia.ads.MiaAd;
import com.ads.mia.ads.MiaAdCallback;
import com.ads.mia.ads.bannerAds.MiaBannerAdView;
import com.ads.mia.ads.nativeAds.MiaNativeAdView;
import com.ads.mia.ads.wrapper.ApAdError;
import com.ads.mia.ads.wrapper.ApInterstitialAd;
import com.ads.mia.ads.wrapper.ApRewardAd;
import com.ads.mia.billing.AppPurchase;
import com.ads.mia.config.MiaAdConfig;
import com.ads.mia.dialog.DialogExitApp1;
import com.ads.mia.dialog.InAppDialog;
import com.ads.mia.event.MiaAdjust;
import com.ads.mia.funtion.AdCallback;
import com.ads.mia.funtion.DialogExitListener;
import com.ads.mia.funtion.PurchaseListener;
import com.mia.module.BuildConfig;
import com.mia.module.R;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.nativead.NativeAd;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";
    private static final String TAG = "MAIN_TEST";

    private static final String EVENT_TOKEN_SIMPLE = "";
    private static final String EVENT_TOKEN_REVENUE = "";


    private FrameLayout frAds;
    private NativeAd unifiedNativeAd;
    private ApInterstitialAd mInterstitialAd;
    private ApRewardAd rewardAd;


    private String idBanner = "";
    private String idNative = "";
    private String idInter = "";

    private int layoutNativeCustom;
    private MiaNativeAdView miaNativeAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        miaNativeAdView = findViewById(R.id.mia_native_ads);


        configMediationProvider();
        MiaAd.getInstance().setCountClickToShowAds(3);

        AppOpenManager.getInstance().setEnableScreenContentCallback(true);
        AppOpenManager.getInstance().setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.e("AppOpenManager", "onAdShowedFullScreenContent: ");

            }
        });

        miaNativeAdView.loadNativeAd(this, idNative, new MiaAdCallback() {
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });


        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
            }

            @Override
            public void onUserCancelBilling() {

            }
        });

        MiaBannerAdView bannerAdView = findViewById(R.id.bannerView);
        bannerAdView.loadBanner(this, idBanner, new MiaAdCallback() {
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
        loadAdInterstitial();

        findViewById(R.id.btShowAds).setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {

                ApInterstitialAd inter = MiaAd.getInstance().getInterstitialAds(this, idInter);

                MiaAd.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new MiaAdCallback() {
                    @Override
                    public void onNextAction() {
                        startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }
        });

        findViewById(R.id.btForceShowAds).setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {
                MiaAd.getInstance().forceShowInterstitial(this, mInterstitialAd, new MiaAdCallback() {
                    @Override
                    public void onNextAction() {
                        startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }

        });

        findViewById(R.id.btnShowReward).setOnClickListener(v -> {
            if (rewardAd != null && rewardAd.isReady()) {
                MiaAd.getInstance().forceShowRewardAd(this, rewardAd, new MiaAdCallback());
                return;
            }
            rewardAd = MiaAd.getInstance().getRewardAd(this, BuildConfig.ad_reward);
        });

        Button btnIAP = findViewById(R.id.btIap);
        if (AppPurchase.getInstance().isPurchased()) {
            btnIAP.setText("Consume Purchase");
        } else {
            btnIAP.setText("Purchase");
        }
        btnIAP.setOnClickListener(v -> {
            if (AppPurchase.getInstance().isPurchased()) {
                AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST);
            } else {
                InAppDialog dialog = new InAppDialog(this);
                dialog.setCallback(() -> {
                    AppPurchase.getInstance().purchase(this, PRODUCT_ID);
                    dialog.dismiss();
                });
                dialog.show();
            }
        });

    }

    private void configMediationProvider() {
        if (MiaAd.getInstance().getMediationProvider() == MiaAdConfig.PROVIDER_ADMOB) {
            idBanner = BuildConfig.ad_banner;
            idNative = BuildConfig.ad_native;
            idInter = BuildConfig.ad_interstitial_splash;
            layoutNativeCustom = com.ads.mia.R.layout.custom_native_admod_medium_rate;
        } else {
            idBanner = getString(R.string.applovin_test_banner);
            idNative = getString(R.string.applovin_test_native);
            idInter = getString(R.string.applovin_test_inter);
            layoutNativeCustom = com.ads.mia.R.layout.custom_native_max_medium;
        }
    }

    private void loadAdInterstitial() {

        mInterstitialAd = MiaAd.getInstance().getInterstitialAds(this, idInter);
    }


    public void onTrackSimpleEventClick(View v) {
        MiaAdjust.onTrackEvent(EVENT_TOKEN_SIMPLE);
    }

    public void onTrackRevenueEventClick(View v) {
        MiaAdjust.onTrackRevenue(EVENT_TOKEN_REVENUE, 1f, "EUR");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNativeExit();
    }

    private void loadNativeExit() {

        if (unifiedNativeAd != null)
            return;
        Admob.getInstance().loadNativeAd(this, BuildConfig.ad_native, new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(NativeAd unifiedNativeAd) {
                MainActivity.this.unifiedNativeAd = unifiedNativeAd;
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        }, MiaAdConfig.ADJUST_TOKEN_TIKTOK);
    }

    @Override
    public void onBackPressed() {
        if (unifiedNativeAd == null)
            return;

        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd, 3);
        dialogExitApp1.setDialogExitListener(new DialogExitListener() {
            @Override
            public void onExit(boolean exit) {
                MainActivity.super.onBackPressed();
            }
        });
        dialogExitApp1.setCancelable(false);
        dialogExitApp1.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}