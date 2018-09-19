package com.davesla.progressgif.listener;

import android.graphics.Bitmap;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public interface GifProcessListener {

    void onStart();

    void onProgress(Bitmap bitmap, float progress);

    void onError(Exception e);

    void onComplete();

}
