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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

public class BatteryStatusAppWidget1x1Provider extends AppWidgetProvider {

    private static int layoutResId = R.layout.widget_1x1;

    static boolean status = false;

    @Override
    public void onEnabled(Context context) {
        //Log.d("AppWidgetProvider", "onEnabled");
        super.onEnabled(context);

        status = true;

        // サービス起動
        context.startService(new Intent(context.getApplicationContext(), BatteryStatusAppWidget1x1Provider.AppWidget1x1UpdateService.class));

        // 更新
        pushAppWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // スクリーンロックを検知
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT) && status) {
            context.startService(new Intent(context.getApplicationContext(), BatteryStatusAppWidget1x1Provider.AppWidget1x1UpdateService.class));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Log.d("AppWidgetProvider", "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        status = true;

        // サービス起動
        context.startService(new Intent(context.getApplicationContext(), BatteryStatusAppWidget1x1Provider.AppWidget1x1UpdateService.class));

        // 更新
        pushAppWidget(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //Log.d("AppWidgetProvider", "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        //Log.d("AppWidgetProvider", "onDisabled");

        status = false;
        context.stopService(new Intent(context.getApplicationContext(), BatteryStatusAppWidget1x1Provider.AppWidget1x1UpdateService.class));

        super.onDisabled(context);
    }

//    private synchronized static void pushAppWidget(Context context, Intent intent) {
//        Log.d("WidgetProvider", "pushAppWidget: intent = " + intent.getAction());
//        // ウィジェットレイアウトの初期化
//        if (intent.getAction().equals(AppWidgetUpdateService.ACTION_APP_WIDGET_UPDATE_SERVICE)) {
//            RemoteViews rv = new RemoteViews(context.getPackageName(), layoutResId);
//            AppWidgetMethods.buildAppWidget(context, intent, rv);
//            updateAppWidget(context, rv);
//        }
//    }

    private synchronized static void pushAppWidget(Context context) {
        //Log.d("WidgetProvider", "pushAppWidget");
        // ウィジェットレイアウトの初期化
        RemoteViews rv = new RemoteViews(context.getPackageName(), layoutResId);
        Intent intent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        AppWidgetMethods.buildAppWidget(context, intent, rv);
        updateAppWidget(context, rv);
    }

    private synchronized static void updateAppWidget(Context context, RemoteViews rv) {
        // タッチイベント「電池使用量」を開く
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(Intent.ACTION_POWER_USAGE_SUMMARY), 0);
        rv.setOnClickPendingIntent(R.id.frameLayout_widgetBackground, pendingIntent);

        ComponentName cn = new ComponentName(context, BatteryStatusAppWidget1x1Provider.class);
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        awm.updateAppWidget(cn, rv);
    }


    public static class AppWidget1x1UpdateService extends Service {

        final IWidget1x1AidlInterface.Stub binder = new IWidget1x1AidlInterface.Stub() { };

        public AppWidget1x1UpdateService() {
        }

        @Override
        public void onCreate() {
            super.onCreate();
            registerBroadcastReceiver(getApplicationContext());
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            //Log.d("AppWidgetUpdateService", "onStartCommand: Intent = " + intent);
            super.onStartCommand(intent, flags, startId);

            //Log.d("AppWidgetProvider", "status = " + status);
            if (!status) {
                return START_NOT_STICKY;
            }

            if (intent != null) {
                try {
                    Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
                    restartServiceIntent.setPackage(getPackageName());
                    PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + DebugActivity.getAppWidgetServiceRestartCycle(getApplicationContext()), restartServicePendingIntent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        super.onTaskRemoved(intent);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            return START_STICKY;        // 死ににくいように
        }

        @Override
        public void onDestroy() {
            //Log.d("AppWidgetUpdateService", "onDestroy");
            unregisterBroadcastReceiver(getApplicationContext());
            super.onDestroy();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return binder;
        }

        private static BroadcastReceiver mReceiver;
        static void registerBroadcastReceiver(Context context) {
            if (mReceiver == null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                mReceiver = new AppWidget1x1BroadcastReceiver();
                context.registerReceiver(mReceiver, filter);
            }
        }
        static void unregisterBroadcastReceiver(Context context) {
            if (mReceiver != null) {
                try {
                    context.unregisterReceiver(mReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mReceiver = null;
                }
            }
        }
    }

    public static class AppWidget1x1BroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d("AppWidgetUpdateService", "onReceive: action = " + action);
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 更新
                pushAppWidget(context);
            }
        }
    };

}