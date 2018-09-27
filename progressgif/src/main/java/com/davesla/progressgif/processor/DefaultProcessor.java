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
    private Position position;
    private int backgroundColor;
    private int backgroundWidth;

    public DefaultProcessor() {
        color = Color.WHITE;
        strokeWidth = 2;
        backgroundColor = Color.argb(180, 0, 0, 0);
        backgroundWidth = 3;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public void setBackgroundWidth(int backgroundWidth) {
        this.backgroundWidth = backgroundWidth;
    }

    @Override
    public Bitmap process(Bitmap originBitmap, float progress) {
        Bitmap bitmap = Bitmap.createBitmap(originBitmap);

        Canvas canvas = new Canvas(bitmap);
        Paint progressPaint = new Paint();
        progressPaint.setColor(color);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeWidth(backgroundWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, 0, 0, progressPaint);
        if (position == Position.BOTTOM) {
            //draw background
            canvas.drawLine(0, originBitmap.getHeight() - strokeWidth / 2,
                    originBitmap.getWidth() , originBitmap.getHeight() - strokeWidth / 2, backgroundPaint);

            //draw progress
            canvas.drawLine(0, originBitmap.getHeight() - strokeWidth / 2,
                    originBitmap.getWidth() * progress, originBitmap.getHeight() - strokeWidth / 2, progressPaint);
        } else {
            //draw background
            canvas.drawLine(0, strokeWidth / 2,
                    originBitmap.getWidth(), strokeWidth / 2, progressPaint);

            //draw progress
            canvas.drawLine(0, strokeWidth / 2,
                    originBitmap.getWidth() * progress, strokeWidth / 2, progressPaint);
        }

        originBitmap.recycle();
        return bitmap;
    }
}
