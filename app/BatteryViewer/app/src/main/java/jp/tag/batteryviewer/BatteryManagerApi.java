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

import android.content.Intent;
import android.os.BatteryManager;

public class BatteryManagerApi {

    // 文字列
    public static final int[] STR_HEALTH = { R.string.battery_health_unknown, R.string.battery_health_unknown, R.string.battery_health_good, R.string.battery_health_overheat, R.string.battery_health_dead, R.string.battery_health_over_voltage, R.string.battery_health_unspecified_failure };
    public static final int[] STR_PLUGGED = { 0, R.string.battery_plugged_ac, R.string.battery_plugged_usb, R.string.battery_plugged_wireless };
    public static final int[] STR_STATUS ={ R.string.battery_status_unknown, R.string.battery_status_unknown, R.string.battery_status_charging, R.string.battery_status_discharging, R.string.battery_status_not_charging, R.string.battery_status_full };

    // アイコン
    public static final int[] ICON_PLUGGED = { R.drawable.ic_unknown, R.drawable.ic_ac, R.drawable.ic_usb, R.drawable.ic_wireless };
    public static final int[] ICON_LEVEL = {
            R.drawable.ic_notification_small_000,
            R.drawable.ic_notification_small_001,
            R.drawable.ic_notification_small_002,
            R.drawable.ic_notification_small_003,
            R.drawable.ic_notification_small_004,
            R.drawable.ic_notification_small_005,
            R.drawable.ic_notification_small_006,
            R.drawable.ic_notification_small_007,
            R.drawable.ic_notification_small_008,
            R.drawable.ic_notification_small_009,
            R.drawable.ic_notification_small_010,
            R.drawable.ic_notification_small_011,
            R.drawable.ic_notification_small_012,
            R.drawable.ic_notification_small_013,
            R.drawable.ic_notification_small_014,
            R.drawable.ic_notification_small_015,
            R.drawable.ic_notification_small_016,
            R.drawable.ic_notification_small_017,
            R.drawable.ic_notification_small_018,
            R.drawable.ic_notification_small_019,
            R.drawable.ic_notification_small_020,
            R.drawable.ic_notification_small_021,
            R.drawable.ic_notification_small_022,
            R.drawable.ic_notification_small_023,
            R.drawable.ic_notification_small_024,
            R.drawable.ic_notification_small_025,
            R.drawable.ic_notification_small_026,
            R.drawable.ic_notification_small_027,
            R.drawable.ic_notification_small_028,
            R.drawable.ic_notification_small_029,
            R.drawable.ic_notification_small_030,
            R.drawable.ic_notification_small_031,
            R.drawable.ic_notification_small_032,
            R.drawable.ic_notification_small_033,
            R.drawable.ic_notification_small_034,
            R.drawable.ic_notification_small_035,
            R.drawable.ic_notification_small_036,
            R.drawable.ic_notification_small_037,
            R.drawable.ic_notification_small_038,
            R.drawable.ic_notification_small_039,
            R.drawable.ic_notification_small_040,
            R.drawable.ic_notification_small_041,
            R.drawable.ic_notification_small_042,
            R.drawable.ic_notification_small_043,
            R.drawable.ic_notification_small_044,
            R.drawable.ic_notification_small_045,
            R.drawable.ic_notification_small_046,
            R.drawable.ic_notification_small_047,
            R.drawable.ic_notification_small_048,
            R.drawable.ic_notification_small_049,
            R.drawable.ic_notification_small_050,
            R.drawable.ic_notification_small_051,
            R.drawable.ic_notification_small_052,
            R.drawable.ic_notification_small_053,
            R.drawable.ic_notification_small_054,
            R.drawable.ic_notification_small_055,
            R.drawable.ic_notification_small_056,
            R.drawable.ic_notification_small_057,
            R.drawable.ic_notification_small_058,
            R.drawable.ic_notification_small_059,
            R.drawable.ic_notification_small_060,
            R.drawable.ic_notification_small_061,
            R.drawable.ic_notification_small_062,
            R.drawable.ic_notification_small_063,
            R.drawable.ic_notification_small_064,
            R.drawable.ic_notification_small_065,
            R.drawable.ic_notification_small_066,
            R.drawable.ic_notification_small_067,
            R.drawable.ic_notification_small_068,
            R.drawable.ic_notification_small_069,
            R.drawable.ic_notification_small_070,
            R.drawable.ic_notification_small_071,
            R.drawable.ic_notification_small_072,
            R.drawable.ic_notification_small_073,
            R.drawable.ic_notification_small_074,
            R.drawable.ic_notification_small_075,
            R.drawable.ic_notification_small_076,
            R.drawable.ic_notification_small_077,
            R.drawable.ic_notification_small_078,
            R.drawable.ic_notification_small_079,
            R.drawable.ic_notification_small_080,
            R.drawable.ic_notification_small_081,
            R.drawable.ic_notification_small_082,
            R.drawable.ic_notification_small_083,
            R.drawable.ic_notification_small_084,
            R.drawable.ic_notification_small_085,
            R.drawable.ic_notification_small_086,
            R.drawable.ic_notification_small_087,
            R.drawable.ic_notification_small_088,
            R.drawable.ic_notification_small_089,
            R.drawable.ic_notification_small_090,
            R.drawable.ic_notification_small_091,
            R.drawable.ic_notification_small_092,
            R.drawable.ic_notification_small_093,
            R.drawable.ic_notification_small_094,
            R.drawable.ic_notification_small_095,
            R.drawable.ic_notification_small_096,
            R.drawable.ic_notification_small_097,
            R.drawable.ic_notification_small_098,
            R.drawable.ic_notification_small_099,
            R.drawable.ic_notification_small_100
    };

    public static int getLevelIcon(Intent intent) {
        return ICON_LEVEL[getBatteryRemain(intent)];
    }

    /**
     * バッテリの存在
     * @param intent
     * @return
     */
    public static boolean getPresent(Intent intent) {
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        //Log.d("BatteryManager", "Present: " + present);
        return present;
    }

    /**
     * 健康状態
     * @param intent
     * @return
     */
    public static int getHealth(Intent intent) {
        int idx = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
//        switch (idx) {
//            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_UNKNOWN (1)");
//                break;
//            case BatteryManager.BATTERY_HEALTH_GOOD:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_GOOD (2)");
//                break;
//            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_OVERHEAT (3)");
//                break;
//            case BatteryManager.BATTERY_HEALTH_DEAD:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_DEAD (4)");
//                break;
//            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_OVER_VOLTAGE (5)");
//                break;
//            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
//                Log.d("BatteryManager", "Health: BATTERY_HEALTH_UNSPECIFIED_FAILURE (6)");
//                break;
//        }
        return idx;
    }

    /**
     * 小さいアイコン
     * @param intent
     * @return
     */
    public static int getIconSmall(Intent intent) {
        int icon = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1);
        //Log.d("BatteryManager", "Icon-small: " + icon);
        return icon;
    }

    /**
     * バッテリレベルの算出
     * @param intent
     * @return
     */
    public static int getBatteryRemain(Intent intent) {
        int level = getLevel(intent);
        int scale = getScale(intent);
        return (int)((float)level * 100 / scale);
    }

    /**
     * 残量
     * @param intent
     * @return
     */
    public static int getLevel(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //Log.d("BatteryManager", "Level: " + level);
        return level;
    }

    /**
     * 充電方法
     * @param intent
     * @return
     */
    public static int getPlugged(Intent intent) {
        int idx = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
//        switch (idx) {
//            case BatteryManager.BATTERY_PLUGGED_AC:
//                Log.d("BatteryManager", "Plugged: BATTERY_PLUGGED_AC (1)");
//                break;
//            case BatteryManager.BATTERY_PLUGGED_USB:
//                Log.d("BatteryManager", "Plugged: BATTERY_PLUGGED_USB (2)");
//                break;
//            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
//                Log.d("BatteryManager", "Plugged: BATTERY_PLUGGED_WIRELESS (4) [API level 17]");
//                break;
//        }
        return idx;
    }

    /**
     * 残量のスケール
     * @param intent
     * @return
     */
    public static int getScale(Intent intent) {
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //Log.d("BatteryManager", "Scale: " + scale);
        return scale;
    }

    /**
     * 状態
     * @param intent
     * @return
     */
    public static int getStatus(Intent intent) {
        int idx = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
//        switch (idx) {
//            case BatteryManager.BATTERY_STATUS_UNKNOWN:
//                Log.d("BatteryManager", "Status: BATTERY_STATUS_UNKNOWN (1)");
//                break;
//            case BatteryManager.BATTERY_STATUS_CHARGING:
//                Log.d("BatteryManager", "Status: BATTERY_STATUS_CHARGING (2)");
//                break;
//            case BatteryManager.BATTERY_STATUS_DISCHARGING:
//                Log.d("BatteryManager", "Status: BATTERY_STATUS_DISCHARGING (3)");
//                break;
//            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//                Log.d("BatteryManager", "Status: BATTERY_STATUS_NOT_CHARGING (4)");
//                break;
//            case BatteryManager.BATTERY_STATUS_FULL:
//                Log.d("BatteryManager", "Status: BATTERY_STATUS_FULL (5)");
//                break;
//        }
        return idx;
    }

    /**
     * 種類
     * @param intent
     * @return
     */
    public static String getTechnology(Intent intent) {
        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        //Log.d("BatteryManager", "Technology: " + technology);
        return technology;
    }

    /**
     * 温度
     * @param intent
     * @return
     */
    public static int getTemperature(Intent intent) {
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE , 0);
        //Log.d("BatteryChange", "Temperature: " + temperature + " [10e-1 degree Celsius]");
        return temperature;
    }

    /**
     * 電圧
     * @param intent
     * @return
     */
    public static int getVoltage(Intent intent) {
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        //Log.d("BatteryChange", "Voltage: " + voltage + " [mV]");
        return voltage;
    }
}
