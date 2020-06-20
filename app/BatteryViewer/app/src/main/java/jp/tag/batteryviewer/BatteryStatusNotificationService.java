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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

public class BatteryStatusNotificationService extends Service {

    private int NOTIFICATION_ID = 55;
    private BroadcastReceiver mReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d("NotificationService", "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        // レシーバの初期化
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);  // バッテリ状態
        mReceiver = new ActionReceiver();
        registerReceiver(mReceiver, filter);

        // 通知
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, createNotification(getApplicationContext()));
        nm = null;
        //startForeground(NOTIFICATION_ID, createNotification(getApplicationContext()));

        return START_STICKY;
    }

    public void onDestroy() {
        //Log.d("NotificationService", "onDestroy");
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mReceiver = null;
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        nm = null;

        super.onDestroy();
    }

    /**
     * レシーバ
     */
    public class ActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d("BatteryReceiver", "Action = " + action);

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 通知
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(NOTIFICATION_ID, createNotification(getApplicationContext(), intent));
                nm = null;
            }

        }
        public ActionReceiver() {
        }
    }
    private Notification createNotification(Context context) {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return createNotification(context, intent);
    }

    private Notification createNotification(Context context, Intent intent) {
        //Log.d("NotificationService", "createNotification");
        // IDs
        int WIDGET_ID = 555;

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, WIDGET_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = context.getResources();

        String strLevel = String.valueOf(BatteryManagerApi.getBatteryRemain(intent));

        int status = BatteryManagerApi.getStatus(intent);
        String strStatus = getString(BatteryManagerApi.STR_STATUS[status]);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            strStatus += " (" +  getString(BatteryManagerApi.STR_PLUGGED[status]) + ")";
        }

        // 通知領域に表示
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent);        // タップ時に呼び出す Activity
        builder.setTicker(res.getString(R.string.app_name));                       // ステータスバーに表示されるテキスト
        builder.setContentTitle(strLevel + "%");                 // タイトル
        builder.setContentText(strStatus);            // タイトル下の説明文
        builder.setSmallIcon(BatteryManagerApi.getLevelIcon(intent)); // アイコン
        //builder.setLargeIcon( BitmapUtilities.createSquareTextIcon(strLevel, 24) );

        builder.setWhen(0);   // 通知タイミング

        // Android 4.1 Jelly Bean 以降
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);   // 優先度: デフォルト
        }

        // Android 5.0 Lollipop 以降
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);   // プライベート
            builder.setCategory(NotificationCompat.CATEGORY_SERVICE);       // カテゴリ: サービス
        }

        Notification notification = builder.build();   // (ビルド)
        notification.flags |= NotificationCompat.FLAG_NO_CLEAR;  // 常時表示

        // Android 4.3 未満
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            notification.icon = 0;  // 通知非表示
//        }

        activityIntent = null;
        contentIntent = null;
        res = null;
        builder = null;

        return notification;
    }
}
