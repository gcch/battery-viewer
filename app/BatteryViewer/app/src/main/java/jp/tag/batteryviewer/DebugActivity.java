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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        updateAppWidgetUpdateTable();

        TextView tvAppWidgetRestartCycle = (TextView) findViewById(R.id.textView_AppWidgetServiceRestartCycleValue);
        tvAppWidgetRestartCycle.setText(String.valueOf(getAppWidgetServiceRestartCycle(getApplicationContext())));
        TableRow trAppWidgetRestartCycle = (TableRow) findViewById(R.id.tableRow_AppWidgetServiceRestartCycle);
        trAppWidgetRestartCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                // View
                LayoutInflater inflater = LayoutInflater.from(context);
                LinearLayout llDialogView = (LinearLayout) inflater.inflate(R.layout.dialog_edit_text, new LinearLayout(context));

                // 入力テキスト部分
                final EditText etInput = (EditText) llDialogView.findViewById(R.id.editText_input);
                etInput.setText(String.valueOf(getAppWidgetServiceRestartCycle(getApplicationContext())), EditText.BufferType.NORMAL);

                // 単位部分
                TextView tvUnit = (TextView) llDialogView.findViewById(R.id.textView_unit);
                tvUnit.setText(R.string.text_milliseconds);

                // Dialog
                AlertDialog.Builder adB = new AlertDialog.Builder(DebugActivity.this);
                adB.setTitle(getText(R.string.app_widget_service_restart_cycle));       // タイトル
                adB.setView(llDialogView);
                adB.setPositiveButton(getText(R.string.text_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String strInput = etInput.getText().toString();
                        int input = Integer.parseInt(strInput);
                        putAppWidgetServiceRestartCycle(getApplicationContext(), input);
                        TextView tvAppWidgetRestartCycle = (TextView) findViewById(R.id.textView_AppWidgetServiceRestartCycleValue);
                        tvAppWidgetRestartCycle.setText(strInput);
                    }
                });
                adB.setNegativeButton(getText(R.string.text_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adB.show();

                context = null;
                inflater = null;
                llDialogView = null;
                adB = null;
            }
        });
    }

    private void updateAppWidgetUpdateTable() {
        TextView tvAppWidget1x1 = (TextView) findViewById(R.id.textView_AppWidget1x1UpdateServiceValue);
        tvAppWidget1x1.setText(isServiceRunning(getApplicationContext(), BatteryStatusAppWidget1x1Provider.AppWidget1x1UpdateService.class) ? "Running" : "Stopped");

        TextView tvAppWidget2x1 = (TextView) findViewById(R.id.textView_AppWidget2x1UpdateServiceValue);
        tvAppWidget2x1.setText(isServiceRunning(getApplicationContext(), BatteryStatusAppWidget2x1Provider.AppWidget2x1UpdateService.class) ? "Running" : "Stopped");
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

    private static final String KEY_APP_WIDGET_SERVICE_RESTART_CYCLE = "kAppWdgSrvResC";
    private static final int DEFAULT_APP_WIDGET_SERVICE_RESTART_CYCLE = 7200000;      // デフォルト: 2時間
    public static void putAppWidgetServiceRestartCycle(Context context, int milliseconds) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(KEY_APP_WIDGET_SERVICE_RESTART_CYCLE, milliseconds);
        ed.commit();
    }
    public static int getAppWidgetServiceRestartCycle(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(KEY_APP_WIDGET_SERVICE_RESTART_CYCLE, DEFAULT_APP_WIDGET_SERVICE_RESTART_CYCLE);
    }

}
