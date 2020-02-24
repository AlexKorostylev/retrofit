package com.retrofit.webcloaca;

import android.app.Application;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerConversionListener;

import java.util.Map;

public class AFApplication extends Application {
    private static final String AF_DEV_KEY = "SD5eTTsnm3bCSMoMRr92XJ";

    @Override
    public void onCreate() {
        super.onCreate();
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {

            }

            @Override
            public void onConversionDataFail(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {

                for (String attrName : conversionData.keySet()) {
                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        };


        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);

    }
}
