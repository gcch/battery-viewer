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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

/**
 * Created by tag on 2015/11/25.
 */
public class BitmapUtilities {

    public static Bitmap createSquareTextIcon(String text, int sideLength) {

        Rect rect = new Rect();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.getTextBounds(text, 0, text.length(), rect);

        FontMetrics fm = paint.getFontMetrics();
        int mtw = (int) paint.measureText(text);
        int fmHeight = (int) (Math.abs(fm.top) + fm.bottom);

        // 描画領域ピッタリのビットマップ作成
        int margin = 1; // ギリギリすぎるので上下左右に多少余裕を取る
        Bitmap bmpText = Bitmap.createBitmap(mtw + margin * 2, fmHeight + margin * 2, Bitmap.Config.ARGB_8888);

        Bitmap bmp = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888);

        // ビットマップからキャンバスを作成してテキスト描画
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(text, margin, Math.abs(fm.ascent) + margin, paint);


        return bmp;
    }

    public static Bitmap generateBitmap(Resources res, int resId, int width, int height, float density) {

        float scaledWidth = width * density;
        float scaledHeight = height * density;
        //Log.d("BitmapUtilities", "scaledSize: " + scaledWidth + ", " + scaledHeight);

        // 画像の生成
        BitmapFactory.Options bfOpt = new BitmapFactory.Options();

        // 画像倍率の取得
        bfOpt.inJustDecodeBounds = true;    // 大きさだけ
        int sampleSizeWidth = (int) (bfOpt.outWidth / scaledWidth);
        int sampleSizeHeight = (int) (bfOpt.outHeight / scaledHeight);
        int sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
        bfOpt.inSampleSize = sampleSize;

        // 実際の画像の取得
        bfOpt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeResource(res, resId, bfOpt);
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        float scale = Math.min((float)scaledWidth / bmpWidth, (float)scaledHeight / bmpHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

        bfOpt = null;
        matrix = null;

        return bmp;
    }
}
