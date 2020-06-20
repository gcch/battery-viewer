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

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.RemoteViews;

public class AppWidgetMethods {

    public static final int[][] BATTERY_REMAINED_RES = {
            { 10, R.drawable.ic_battery_levels_0_of_5, R.drawable.widget_background_low},
            { 20, R.drawable.ic_battery_levels_1_of_5, R.drawable.widget_background_medium},
            { 40, R.drawable.ic_battery_levels_2_of_5, R.drawable.widget_background_medium},
            { 50, R.drawable.ic_battery_levels_3_of_5, R.drawable.widget_background_medium},
            { 60, R.drawable.ic_battery_levels_3_of_5, R.drawable.widget_background_high},
            { 80, R.drawable.ic_battery_levels_4_of_5, R.drawable.widget_background_high},
            {100, R.drawable.ic_battery_levels_5_of_5, R.drawable.widget_background_high}
    };

    /**
     * バッテリ残量の取得＆ RemoteViews の TextView の初期化
     * @param context
     * @param rv
     */
    public synchronized static void buildAppWidget(Context context, Intent intent, RemoteViews rv) {
        //Log.d("AppWidgetMethods", "buildAppWidget");
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
                for (int i = 0; i < BATTERY_REMAINED_RES.length; i++) {
                    if (level <= BATTERY_REMAINED_RES[i][0]) {
                        iconResId = BATTERY_REMAINED_RES[i][1];
                        backgroundResId = BATTERY_REMAINED_RES[i][2];
                        break;
                    }
                }
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                iconResId = R.drawable.ic_done;
                backgroundResId = R.drawable.widget_background_high;
                break;
        }

        // 残量の更新
        rv.setTextViewText(R.id.textView_batteryRemain, level + "%");
        // アイコンの更新
        rv.setImageViewResource(R.id.imageView_icon, iconResId);
        // 背景部分の更新
        rv.setInt(R.id.frameLayout_widgetBackground, "setBackgroundResource", backgroundResId);
    }
}
