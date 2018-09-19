package com.davesla.progressgif.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public class SquareProgressProcessor implements Processor {
    public enum Position {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT
    }

    private int color;
    private int strokeWidth;
    private float opacity;
    private Position startPosition;

    public SquareProgressProcessor() {
        color = Color.parseColor("#f74249");
        strokeWidth = 4;
        opacity = 0.95f;
        startPosition = Position.TOP_CENTER;
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
        this.strokeWidth = strokeWidth * 2;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        if (opacity < 0) {
            this.opacity = 0;
        } else if (opacity > 1) {
            this.opacity = 1;
        } else {
            this.opacity = opacity;
        }
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public Bitmap process(Bitmap originBitmap, float progress) {
        Bitmap bitmap = Bitmap.createBitmap(originBitmap);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setAlpha((int) (255 * opacity));
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawPath(getPath(originBitmap, progress), paint);

        originBitmap.recycle();
        return bitmap;
    }

    private Path getPath(Bitmap bitmap, float progress) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int girth = (width + height) * 2;
        int currentLength = (int) (progress * girth);

        Path path = new Path();

        switch (startPosition) {
            case TOP_LEFT:
                if (currentLength <= width) {
                    path.lineTo(currentLength, 0);
                } else if (currentLength <= (width + height)) {
                    path.lineTo(width, 0);
                    path.lineTo(width, currentLength - width);
                } else if (currentLength <= width * 2 + height) {
                    path.lineTo(width, 0);
                    path.lineTo(width, height);
                    path.lineTo(width * 2 + height - currentLength, height);
                } else {
                    path.lineTo(width, 0);
                    path.lineTo(width, height);
                    path.lineTo(0, height);
                    path.lineTo(0, girth - currentLength);
                }
                break;
            case TOP_CENTER:
                float halfWidth = ((float) width) / 2;
                path.moveTo(halfWidth, 0);
                if (currentLength <= halfWidth) {
                    path.rLineTo(currentLength, 0);
                } else if (currentLength <= halfWidth + height) {
                    path.rLineTo(halfWidth, 0);
                    path.rLineTo(0, currentLength - halfWidth);
                } else if (currentLength <= width * 3 / 2 + height) {
                    path.rLineTo(halfWidth, 0);
                    path.rLineTo(0, height);
                    path.rLineTo((halfWidth + height) - currentLength, 0);
                } else if (currentLength <= width * 3 / 2 + height * 2) {
                    path.rLineTo(halfWidth, 0);
                    path.rLineTo(0, height);
                    path.rLineTo(-width, 0);
                    path.rLineTo(0, (width * 3 / 2 + height) - currentLength);
                } else {
                    path.rLineTo(halfWidth, 0);
                    path.rLineTo(0, height);
                    path.rLineTo(-width, 0);
                    path.rLineTo(0, -height);
                    path.rLineTo(currentLength - height * 2 - width * 3 / 2, 0);
                }
                break;
            case TOP_RIGHT:
                path.moveTo(width, 0);
                if (currentLength <= height) {
                    path.rLineTo(0, currentLength);
                } else if (currentLength <= height + width) {
                    path.rLineTo(0, height);
                    path.rLineTo(height - currentLength, 0);
                } else if (currentLength <= height * 2 + width) {
                    path.rLineTo(0, height);
                    path.rLineTo(-width, 0);
                    path.rLineTo(0, height + width - currentLength);
                } else {
                    path.rLineTo(0, height);
                    path.rLineTo(-width, 0);
                    path.rLineTo(0, -height);
                    path.rLineTo(currentLength - (height * 2 + width), 0);
                }
                break;
        }
        return path;
    }
}
