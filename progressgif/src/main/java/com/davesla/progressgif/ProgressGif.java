package com.davesla.progressgif;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.davesla.progressgif.listener.GifProcessListener;
import com.davesla.progressgif.processor.DefaultProcessor;
import com.davesla.progressgif.processor.Processor;
import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.FileNotFoundException;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public class ProgressGif {
    private ProgressParams params;
    private GifDecoder gifDecoder;
    private GifEncoder gifEncoder;

    private ProgressGif() {
        gifDecoder = new GifDecoder();
        gifEncoder = new GifEncoder();
    }

    private void setParams(ProgressParams params) {
        if (checkParams(params)) {
            this.params = params;
        }
    }

    private boolean checkParams(ProgressParams params) {
        if (params.processor == null) {
            params.processor = new DefaultProcessor();
        }

        if (params.gifQuality == null) {
            params.gifQuality = ProgressParams.GifQuality.HIGH;
        }

        if (params.scale <= 0 || params.scale > 1) {
            params.scale = 1.f;
        }

        if (TextUtils.isEmpty(params.destPath)) {
            if (params.listener != null) {
                params.listener.onError(new Exception("invalid destPath!!!"));
            }
            return false;
        }

        if (TextUtils.isEmpty(params.savePath)) {
            if (params.listener != null) {
                params.listener.onError(new Exception("invalid savePath!!!"));
            }
            return false;
        }
        return true;
    }

    public void start() {
        boolean isSucceeded = gifDecoder.load(params.destPath);
        boolean hasInit = false;
        if (isSucceeded) {
            if (params.listener != null) {
                params.listener.onStart();
            }
            for (int i = 0; i < gifDecoder.frameNum(); i++) {
                Bitmap bitmap = gifDecoder.frame(i);
                if (!hasInit) {
                    try {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (params.scale != 1) {
                            width = Math.round(width * params.scale);
                            height = Math.round(height * params.scale);
                        }
                        gifEncoder.init(width, height, params.savePath, covertType(params.gifQuality));
                    } catch (FileNotFoundException e) {
                        if (params.listener != null) {
                            params.listener.onError(e);
                        }
                        return;
                    }
                    hasInit = true;
                }
                float progress = (i + 1) / (float) gifDecoder.frameNum();

                //resize the bitmap
                if (params.scale != 1) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(params.scale, params.scale);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                Bitmap newBitmap = params.processor.process(bitmap, progress);
                gifEncoder.encodeFrame(newBitmap, gifDecoder.delay(i));
                if (params.listener != null) {
                    params.listener.onProgress(newBitmap, progress);
                }
            }
            gifEncoder.close();
            if (params.listener != null) {
                params.listener.onComplete();
            }
        }
    }

    public void startAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }).start();
    }

    private GifEncoder.EncodingType covertType(ProgressParams.GifQuality gifQuality) {
        switch (gifQuality) {
            case LOW:
                return GifEncoder.EncodingType.ENCODING_TYPE_SIMPLE_FAST;
            case MEDIUM:
                return GifEncoder.EncodingType.ENCODING_TYPE_FAST;
            case HIGH:
                return GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY;
        }
        return GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY;
    }

    public static class Builder {
        private final ProgressParams params;

        public Builder() {
            params = new ProgressParams();
        }

        public Builder setSavePath(String savePath) {
            params.savePath = savePath;
            return this;
        }

        public Builder setDestPath(String destPath) {
            params.destPath = destPath;
            return this;
        }

        public Builder setProcessListener(GifProcessListener listener) {
            params.listener = listener;
            return this;
        }

        public Builder setGifQuality(ProgressParams.GifQuality gifQuality) {
            params.gifQuality = gifQuality;
            return this;
        }

        public Builder setScale(float scale) {
            params.scale = scale;
            return this;
        }

        public Builder setProcessor(Processor processor) {
            params.processor = processor;
            return this;
        }

        public ProgressGif build() {
            ProgressGif progressGif = new ProgressGif();
            progressGif.setParams(params);
            return progressGif;
        }
    }
}
