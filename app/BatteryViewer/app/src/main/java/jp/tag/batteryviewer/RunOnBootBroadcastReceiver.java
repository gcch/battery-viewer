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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RunOnBootBroadcastReceiver extends BroadcastReceiver {
    public static final String KEY_BOOT_NOTIFICATION = "kBootNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "onReceive: " + intent.getAction(), Toast.LENGTH_LONG).show();
        //Log.d("BroadcastReceiver", "onReceive: action = " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (getIsNotificationEnabled(context)) {
                // 起動
                context.getApplicationContext().startService(new Intent(context, BatteryStatusNotificationService.class));
            }
        }
    }

    public static void putIsNotificationEnabled(Context context, Boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(KEY_BOOT_NOTIFICATION, b);
        ed.commit();
    }

    public static boolean getIsNotificationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(KEY_BOOT_NOTIFICATION, false);
    }
}
