package com.davesla.gifsample;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.davesla.progressgif.ProgressGif;
import com.davesla.progressgif.ProgressParams;
import com.davesla.progressgif.listener.GifProcessListener;
import com.davesla.progressgif.processor.DefaultProcessor;
import com.davesla.progressgif.processor.Processor;
import com.davesla.progressgif.processor.SquareProgressProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends AppCompatActivity {
    private Processor processor;
    private ImageView imageView;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);

        final String srcFile = setupSampleFile();
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProcessing) {
                    return;
                }
                String savePath = getExternalFilesDir(DIRECTORY_PICTURES) + File.separator + System.currentTimeMillis() + ".gif";
                process(savePath, srcFile);
            }
        });

        DefaultProcessor defaultProcessor = new DefaultProcessor();
        defaultProcessor.setColor(Color.parseColor("#3399ff"));
        defaultProcessor.setPosition(DefaultProcessor.Position.BOTTOM);
        processor = defaultProcessor;

        ((RadioGroup) findViewById(R.id.radio_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_default) {
                    DefaultProcessor defaultProcessor = new DefaultProcessor();
                    defaultProcessor.setColor(Color.parseColor("#3399ff"));
                    defaultProcessor.setPosition(DefaultProcessor.Position.BOTTOM);
                    processor = defaultProcessor;
                } else {
                    SquareProgressProcessor squareProgressProcessor = new SquareProgressProcessor();
                    squareProgressProcessor.setOpacity(0.9f);
                    squareProgressProcessor.setStartPosition(SquareProgressProcessor.Position.TOP_LEFT);
                    squareProgressProcessor.setStrokeWidth(2);
                    processor = squareProgressProcessor;
                }
            }
        });
    }

    private void process(final String savePath, String destPath) {
        ProgressGif.Builder builder = new ProgressGif.Builder();
        ProgressGif progressGif = builder
                .setDestPath(destPath)
                .setSavePath(savePath)
                .setScale(0.95f)
                .setGifQuality(ProgressParams.GifQuality.MEDIUM)
                .setProcessor(processor)
                .setProcessListener(new GifProcessListener() {
                    @Override
                    public void onStart() {
                        isProcessing = true;
                    }

                    @Override
                    public void onProgress(final Bitmap bitmap, float progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }


                    @Override
                    public void onError(Exception e) {
                        System.out.println("exception:" + e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        isProcessing = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                share(savePath);
                            }
                        });
                    }
                })
                .build();
        progressGif.startAsync();
    }

    private void share(String path) {
        Uri imageUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.davesla.gifsample.fileprovider", new File(path));
        } else {
            imageUri = Uri.fromFile(new File(path));
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "share"));
    }

    private String setupSampleFile() {
        AssetManager assetManager = getAssets();
        String srcFile = "test.gif";
        String destFile = getFilesDir().getAbsolutePath() + File.separator + srcFile;
        File file = new File(destFile);
        if (file.exists()) {
            return destFile;
        }
        copyFile(assetManager, srcFile, destFile);
        return destFile;
    }

    private void copyFile(AssetManager assetManager, String srcFile, String destFile) {
        try {
            InputStream is = assetManager.open(srcFile);
            FileOutputStream os = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
