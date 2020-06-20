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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class BatteryInfoActivity extends AppCompatActivity {

    static TextView tvPresentValue, tvTechnologyValue, tvHealthValue, tvStatusValue, tvLevelValue, tvTemperatureValue, tvVoltageValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_info);

        tvPresentValue = (TextView) findViewById(R.id.textView_presentValue);
        tvTechnologyValue = (TextView) findViewById(R.id.textView_technologyValue);
        tvHealthValue = (TextView) findViewById(R.id.textView_healthValue);
        tvStatusValue = (TextView) findViewById(R.id.textView_statusValue);
        tvLevelValue = (TextView) findViewById(R.id.textView_levelValue);
        tvTemperatureValue = (TextView) findViewById(R.id.textView_temperatureValue);
        tvVoltageValue = (TextView) findViewById(R.id.textView_voltageValue);

        updateTable(getApplicationContext());

//        Button b = (Button) findViewById(R.id.button_update);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateTable(getApplicationContext());
//            }
//        });

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
                updateTable(context);
            }
        }
    };

    static void updateTable(Context context) {
        IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent mIntent = context.registerReceiver(null, mIntentFilter);

        boolean present = BatteryManagerApi.getPresent(mIntent);
        String strPresentValue = present ? context.getString(R.string.battery_present_exist) : context.getString(R.string.battery_present_nil);
        tvPresentValue.setText(strPresentValue);

        if (present) {
            String strTechnologyValue = BatteryManagerApi.getTechnology(mIntent);
            tvTechnologyValue.setText(strTechnologyValue);

            String strHealthValue = context.getString(BatteryManagerApi.STR_HEALTH[BatteryManagerApi.getHealth(mIntent)]);
            tvHealthValue.setText(strHealthValue);

            String strStatusValue = context.getString(BatteryManagerApi.STR_STATUS[BatteryManagerApi.getStatus(mIntent)]);
            if (BatteryManagerApi.getStatus(mIntent) == BatteryManager.BATTERY_STATUS_CHARGING) {
                strStatusValue += " (" + context.getString( BatteryManagerApi.STR_PLUGGED[ BatteryManagerApi.getPlugged(mIntent) ]) + ")";
            }

            tvStatusValue.setText(strStatusValue);

            String strLevelValue = String.valueOf(BatteryManagerApi.getBatteryRemain(mIntent)) + "%";
            tvLevelValue.setText(strLevelValue);

            float degreeCelsius = (float)BatteryManagerApi.getTemperature(mIntent) / 10;
            String strTemperatureValue = String.valueOf(degreeCelsius) + " °C\n" + String.format("%2.1f", degreeCelsius * 1.8f + 32) + " °F";
            tvTemperatureValue.setText(strTemperatureValue);

            String strVoltageValue = String.valueOf(BatteryManagerApi.getVoltage(mIntent)) + " mV";
            tvVoltageValue.setText(strVoltageValue);
        }
    }


}
