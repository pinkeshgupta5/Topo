package com.dhivi.inc.topo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by Manoj on 11/1/2017.
 */

public class CircleTransform implements Transformation {

    private int colorCode = 0;

    public CircleTransform(int color) {
        colorCode = color;
    }

    public CircleTransform() {
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        //paint.setAntiAlias(true);
        paint.setColor(Color.rgb(227, 57, 45));
        paint.setStrokeWidth(2);

        float r = size / 2f;
        canvas.drawCircle(r, r, r-3, paint);
        Paint paint1 = new Paint();
        if (colorCode > 0){
            paint1.setColor(colorCode);
            paint1.setStrokeWidth(20);
            paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint1.setColor(Color.WHITE);
            paint1.setStrokeWidth(5);
            paint1.setStyle(Paint.Style.STROKE);
        }
        //paint1.setAntiAlias(true);
        canvas.drawCircle(r, r, r-3, paint1);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

