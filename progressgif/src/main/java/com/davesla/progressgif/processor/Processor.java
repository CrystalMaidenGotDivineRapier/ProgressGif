package com.davesla.progressgif.processor;

import android.graphics.Bitmap;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public interface Processor {
    Bitmap process(Bitmap originBitmap, float progress);
}
