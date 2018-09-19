package com.davesla.progressgif.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public class DefaultProcessor implements Processor {
    public enum Position {
        TOP,
        BOTTOM
    }

    private int color;
    private int strokeWidth;
    private float alpha;
    private Position position;

    public DefaultProcessor() {
        color = Color.parseColor("#f74249");
        strokeWidth = 2;
        alpha = 0.95f;
        position = Position.BOTTOM;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Bitmap process(Bitmap originBitmap, float progress) {
        Bitmap bitmap = Bitmap.createBitmap(originBitmap);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setAlpha((int) (255 * alpha));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (position == Position.BOTTOM) {
            canvas.drawLine(0, originBitmap.getHeight() - strokeWidth / 2,
                    originBitmap.getWidth() * progress, originBitmap.getHeight() - strokeWidth / 2, paint);
        } else {
            canvas.drawLine(0, strokeWidth / 2,
                    originBitmap.getWidth() * progress, strokeWidth / 2, paint);
        }

        originBitmap.recycle();
        return bitmap;
    }
}
