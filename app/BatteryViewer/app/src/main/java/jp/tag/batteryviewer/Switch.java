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
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public abstract class Switch {

    private LinearLayout llButton;

    final static float OFF_ALPHA = 0.5f;
    final static float ON_ALPHA = 1.0f;
    final static long  DURATION = 200;

    private int iconResId;
    private int textResId;

    abstract void onSwitchOffListener();
    abstract void onSwitchOnListener();

    private boolean status = true;  // ボタンの状態
    public boolean getStatus() {
        return status;
    }
    public void putStatus(boolean b) {
        status = b;
        changeSwitchColor(status, 0L);
    }

    public Switch(int iconResId, int textResId) {
        this.iconResId = iconResId;
        this.textResId = textResId;
    }

    public LinearLayout create(Context context, boolean status) {
        // LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);
        llButton = (LinearLayout) inflater.inflate(R.layout.button_switch, new LinearLayout(context));

        // ImageView
        ImageView ivSwitchIcon = (ImageView) llButton.findViewById(R.id.imageView_icon_switch);
        ivSwitchIcon.setImageResource(iconResId);
        ivSwitchIcon = null;

        changeSwitchColor(status, 0L);

        // TextView
        TextView tv = (TextView) llButton.findViewById(R.id.textView_switch);
        tv.setText(textResId);
        tv = null;

        // FrameLayout
        ToggleButton tb = (ToggleButton) llButton.findViewById(R.id.toggleButton_switch);
        tb.setChecked(status);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d("Switch", "onCheckedChanged: " + isChecked);

                // スイッチの色を変える (内部で内部状態も変更)
                changeSwitchColor(isChecked, DURATION);

                // アクションの実行
                if (isChecked) {
                    onSwitchOnListener();
                } else {
                    onSwitchOffListener();
                }

            }
        });
        tb = null;

        return llButton;
    }

    private void changeSwitchColor(boolean b, long duration) {
        //Log.d("Switch", "changeSwitchColor");
        if (b && !status) {  // ON 状態にする
            //Log.d("Switch", "changeSwitchColor: off -> on");
            AlphaAnimation animationOn = new AlphaAnimation(OFF_ALPHA, ON_ALPHA);
            animationOn.setDuration(duration);
            animationOn.setFillAfter(true);
            animationOn.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    FrameLayout fl = (FrameLayout) llButton.findViewById(R.id.frameLayout_switch);
                    fl.invalidate();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            ImageView ivSwitchIcon = (ImageView) llButton.findViewById(R.id.imageView_icon_switch);
            ivSwitchIcon.startAnimation(animationOn);
            ImageView ivSwitchBg = (ImageView) llButton.findViewById(R.id.imageView_bg_switch);
            ivSwitchBg.startAnimation(animationOn);
            status = true;
        } else if (!b && status) {  // OFF 状態にする
            //Log.d("Switch", "changeSwitchColor: on -> off");
            AlphaAnimation animationOff = new AlphaAnimation(ON_ALPHA, OFF_ALPHA);
            animationOff.setDuration(duration);
            animationOff.setFillAfter(true);
            animationOff.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    FrameLayout fl = (FrameLayout) llButton.findViewById(R.id.frameLayout_switch);
                    fl.invalidate();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            ImageView ivSwitchIcon = (ImageView) llButton.findViewById(R.id.imageView_icon_switch);
            ivSwitchIcon.startAnimation(animationOff);
            ivSwitchIcon = null;
            ImageView ivSwitchBg = (ImageView) llButton.findViewById(R.id.imageView_bg_switch);
            ivSwitchBg.startAnimation(animationOff);
            ivSwitchBg = null;
            status = false;
        }
    }
}
