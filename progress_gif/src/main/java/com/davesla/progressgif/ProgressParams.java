package com.davesla.progressgif;


import com.davesla.progressgif.listener.GifProcessListener;
import com.davesla.progressgif.processor.Processor;

/**
 * @author CrystalMaidenGotDivineRapier
 * niven.yuki@gmail.com
 * created at 2018/9/18
 */
public class ProgressParams {
    public enum GifQuality {
        LOW,
        MEDIUM,
        HIGH,
    }

    public GifQuality gifQuality;
    public Processor processor;
    public GifProcessListener listener;
    public float scale;
    public String savePath;
    public String destPath;
}
