/*
 * Copyright (C) 2015 tag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.tag.batteryviewer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.amazon.device.ads.DefaultAdListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static View widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean status = RunOnBootBroadcastReceiver.getIsNotificationEnabled(getApplicationContext());

        if (status) {
            startBatteryService(getApplicationContext(), BatteryStatusNotificationService.class);
        }

        FrameLayout flSwitchSpace = (FrameLayout) findViewById(R.id.frameLayout_switchSpace);

        Switch sw = new Switch(R.drawable.ic_power, R.string.text_notification) {
            @Override
            void onSwitchOffListener() {
                //Log.d("Switch", "onSwitchOffListener");
                stopBatteryService(getApplicationContext(), BatteryStatusNotificationService.class);
                RunOnBootBroadcastReceiver.putIsNotificationEnabled(getApplicationContext(), false);
            }
            @Override
            void onSwitchOnListener() {
                //Log.d("Switch", "onSwitchOnListener");
                startBatteryService(getApplicationContext(), BatteryStatusNotificationService.class);
                RunOnBootBroadcastReceiver.putIsNotificationEnabled(getApplicationContext(), true);
            }
        };

        FrameLayout.LayoutParams flParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flParams.gravity = Gravity.CENTER;
        flSwitchSpace.addView(sw.create(this, status), flParams);

        LayoutInflater inflater = getLayoutInflater();
        widget = inflater.inflate(R.layout.widget_2x1, null);
        updateSampleWidget(getApplicationContext());

        FrameLayout flWidgetSpace = (FrameLayout) findViewById(R.id.frameLayout_widgetSpace);

        flWidgetSpace.addView(widget);

        onCreateAd();

        registerBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        unregisterBroadcastReceiver();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    // レシーバ
    private static BroadcastReceiver mReceiver;

    /**
     * registerBroadcastReceiver: ブロードキャストレシーバの登録
     * onCreate() とか onResume() で呼ぶ
     */
    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new SampleWidgetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mReceiver, filter);
        }
    }

    /**
     * registerBroadcastReceiver: ブロードキャストレシーバの解除
     * onDestroy() とか onPause() で呼ぶ
     */
    private void unregisterBroadcastReceiver() {
        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                mReceiver = null;
            }
        }
    }

    public static class SampleWidgetBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 更新
                updateSampleWidget(context);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuItem_batteryInfo).setIntent(new Intent(getApplicationContext(), BatteryInfoActivity.class));
        menu.findItem(R.id.menuItem_legends).setIntent(new Intent(getApplicationContext(), LegendsActivity.class));
        menu.findItem(R.id.menuItem_about).setIntent(new Intent(getApplicationContext(), AboutActivity.class));
        return true;
    }

    private void startBatteryService(Context context, Class<?> cls) {
        if (!isServiceRunning(context, cls)) {
            Intent intent = new Intent(context, cls);
            startService(intent);
            intent = null;
        }
    }

    private void stopBatteryService(Context context, Class<?> cls) {
        if (isServiceRunning(context, cls)) {
            Intent intent = new Intent(context, cls);
            stopService(intent);
            intent = null;
        }
    }

    private boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (cls.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        activityManager = null;
        services = null;
        return false;
    }

    static void updateSampleWidget(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = BatteryManagerApi.getBatteryRemain(intent);
        int iconResId = 0;
        int backgroundResId = R.drawable.widget_background_unknown;

        // 充電状態の取得
        int status = BatteryManagerApi.getStatus(intent);
        //Log.d("AppWidgetMethods", "Level = " + level + " / Status = " + context.getString(BatteryManagerApi.STR_STATUS[status]));

        switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                iconResId = R.drawable.ic_unknown;
                backgroundResId = R.drawable.widget_background_unknown;
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                iconResId = BatteryManagerApi.ICON_PLUGGED[BatteryManagerApi.getPlugged(intent)];
                backgroundResId = R.drawable.widget_background_charging;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                for (int i = 0; i < AppWidgetMethods.BATTERY_REMAINED_RES.length; i++) {
                    if (level <= AppWidgetMethods.BATTERY_REMAINED_RES[i][0]) {
                        iconResId = AppWidgetMethods.BATTERY_REMAINED_RES[i][1];
                        backgroundResId = AppWidgetMethods.BATTERY_REMAINED_RES[i][2];
                        break;
                    }
                }
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                iconResId = R.drawable.ic_done;
                backgroundResId = R.drawable.widget_background_high;
                break;
        }

        ((TextView)widget.findViewById(R.id.textView_batteryRemain)).setText(level + "%");
        ((ImageView)widget.findViewById(R.id.imageView_icon)).setImageResource(iconResId);
        widget.findViewById(R.id.frameLayout_widgetBackground).setBackgroundResource(backgroundResId);

    }


    /**
     * Amazon Mobile Ads
     *************************/

    private AdLayout adView; // The ad view used to load and display the ad.
    private static final String APP_KEY = "78e000942b604fb4b062dc3220aee2cd"; // Sample Application Key. Replace this value with your Application Key.
    //private static final String LOG_TAG = "AmazonMobileAds"; // Tag used to prefix all log messages.

    private void onCreateAd() {
        // For debugging purposes enable logging, but disable for production builds.
        AdRegistration.enableLogging(true);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds.
        AdRegistration.enableTesting(true);

        this.adView = (AdLayout) findViewById(R.id.ad_view);
        this.adView.setListener(new AmazonMobileAdsListener());

        try {
            AdRegistration.setAppKey(APP_KEY);
        } catch (final IllegalArgumentException e) {
            //Log.e(LOG_TAG, "IllegalArgumentException thrown: " + e.toString());
            return;
        }

        // Calling load ad in the Activity's onCreate method allows a new ad to be loaded
        // when the application starts and also when the device is rotated.
        loadAd();
    }

    /**
     * Load a new ad.
     */
    private void loadAd() {
        // Load an ad with default ad targeting.
        //this.adView.loadAd();

        // Note: You can choose to provide additional targeting information to modify how your ads
        // are targeted to your users. This is done via an AdTargetingOptions parameter that's passed
        // to the loadAd call. See an example below:
        //
        final AdTargetingOptions adOptions = new AdTargetingOptions();
        adOptions.enableGeoLocation(true);
        this.adView.loadAd(adOptions);
    }

    /**
     * This class is for an event listener that tracks ad lifecycle events.
     * It extends DefaultAdListener, so you can override only the methods that you need.
     */
    class AmazonMobileAdsListener extends DefaultAdListener {
        // onAdLoaded: 広告のロードに成功したら一度呼ばれる
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            //Log.i(LOG_TAG, adProperties.getAdType().toString() + " ad loaded successfully.");
        }

        // onAdFailedToLoad: 広告の読込に失敗したら呼ばれる
        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            //Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
        }

        // onAdExpanded: リッチメディア広告が開かれた後に呼ばれる
        @Override
        public void onAdExpanded(final Ad ad) {
            //Log.i(LOG_TAG, "Ad expanded.");
            // You may want to pause your activity here.
        }

        // onAdCollapsed: リッチメディア広告が閉じられた後に呼ばれる
        @Override
        public void onAdCollapsed(final Ad ad) {
            //Log.i(LOG_TAG, "Ad collapsed.");
            // Resume your activity here, if it was paused in onAdExpanded.
        }
    }

}
