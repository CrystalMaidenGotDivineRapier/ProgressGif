#include "com_waynejo_androidndkgif_GifDecoder.h"
#include "GifDecoder.h"
#include "BitmapIterator.h"
#include <string.h>
#include <wchar.h>
#include <android/bitmap.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeInit
  (JNIEnv *env, jobject)
{
    return (jlong)new GifDecoder();
}

JNIEXPORT void JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeClose
  (JNIEnv *, jobject, jlong handle)
{
    delete (GifDecoder*)handle;
}

JNIEXPORT jboolean JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeLoad
  (JNIEnv * env, jobject, jlong handle, jstring fileName)
{
    const char* fileNameChars = env->GetStringUTFChars(fileName, 0);
    bool result = ((GifDecoder*)handle)->load(fileNameChars);
    env->ReleaseStringUTFChars(fileName, fileNameChars);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeLoadUsingIterator
  (JNIEnv * env, jobject, jlong handle, jstring fileName)
{
    const char* fileNameChars = env->GetStringUTFChars(fileName, 0);
    BitmapIterator* result = ((GifDecoder*)handle)->loadUsingIterator(fileNameChars);
    env->ReleaseStringUTFChars(fileName, fileNameChars);

    return (jlong)result;
}

JNIEXPORT jint JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeGetFrameCount
  (JNIEnv *, jobject, jlong handle)
{
    return ((GifDecoder*)handle)->getFrameCount();
}

JNIEXPORT jobject JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeGetFrame
  (JNIEnv *env, jobject, jlong handle, jint idx)
{
    GifDecoder* decoder = (GifDecoder*)handle;
    int imgWidth = decoder->getWidth();
    int imgHeight = decoder->getHeight();

    // Creating Bitmap Config Class
    jclass bmpCfgCls = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID bmpClsValueOfMid = env->GetStaticMethodID(bmpCfgCls, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject jBmpCfg = env->CallStaticObjectMethod(bmpCfgCls, bmpClsValueOfMid, env->NewStringUTF("ARGB_8888"));

    // Creating a Bitmap Class
    jclass bmpCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMid = env->GetStaticMethodID(bmpCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject jBmpObj = env->CallStaticObjectMethod(bmpCls, createBitmapMid, imgWidth, imgHeight, jBmpCfg);

    void* bitmapPixels;
    if (AndroidBitmap_lockPixels(env, jBmpObj, &bitmapPixels) < 0) {
        return 0;
    }
    uint32_t* src = (uint32_t*) bitmapPixels;
    int stride = imgWidth * 4;
    int pixelsCount = stride * imgHeight;
    memcpy(bitmapPixels, decoder->getFrame(idx), pixelsCount);
    AndroidBitmap_unlockPixels(env, jBmpObj);

    return jBmpObj;
}

JNIEXPORT jint JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeGetDelay
        (JNIEnv *, jobject, jlong handle, jint idx)
{
    return ((GifDecoder*)handle)->getDelay(idx);
}

JNIEXPORT jint JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeGetWidth
  (JNIEnv *, jobject, jlong handle)
{
    return ((GifDecoder*)handle)->getWidth();
}

JNIEXPORT jint JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeGetHeight
  (JNIEnv *, jobject, jlong handle)
{
    return ((GifDecoder*)handle)->getHeight();
}

JNIEXPORT jboolean JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeBitmapIteratorHasNext
  (JNIEnv *, jobject, jlong handle)
{
    return ((BitmapIterator*)handle)->hasNext();
}

JNIEXPORT jobject JNICALL Java_com_waynejo_androidndkgif_GifDecoder_nativeBitmapIteratornext
  (JNIEnv *env, jobject, jlong decoderHandle, jlong iteratorHandle)
{
    const uint32_t* frame = NULL;
    uint32_t delayMs = 0;
    bool result = ((BitmapIterator*)iteratorHandle)->next(&frame, &delayMs);
    if (!result) {
        return NULL;
    }

    GifDecoder* decoder = (GifDecoder*)decoderHandle;
    int imgWidth = decoder->getWidth();
    int imgHeight = decoder->getHeight();

    // Creating Bitmap Config Class
    jclass bmpCfgCls = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID bmpClsValueOfMid = env->GetStaticMethodID(bmpCfgCls, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject jBmpCfg = env->CallStaticObjectMethod(bmpCfgCls, bmpClsValueOfMid, env->NewStringUTF("ARGB_8888"));

    // Creating a Bitmap Class
    jclass bmpCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMid = env->GetStaticMethodID(bmpCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject jBmpObj = env->CallStaticObjectMethod(bmpCls, createBitmapMid, imgWidth, imgHeight, jBmpCfg);

    void* bitmapPixels;
    if (AndroidBitmap_lockPixels(env, jBmpObj, &bitmapPixels) < 0) {
        return NULL;
    }
    uint32_t* src = (uint32_t*) bitmapPixels;
    int stride = imgWidth * 4;
    int pixelsCount = stride * imgHeight;
    memcpy(bitmapPixels, frame, pixelsCount);
    AndroidBitmap_unlockPixels(env, jBmpObj);

    // Creating a GifImageClass
    jclass gifImageCls = env->FindClass("com/waynejo/androidndkgif/GifImage");
    jmethodID gifImageClsInit = env->GetMethodID(gifImageCls, "<init>", "(Landroid/graphics/Bitmap;I)V");
    return env->NewObject(gifImageCls, gifImageClsInit, jBmpObj, delayMs);
}

#ifdef __cplusplus
}
#endif
