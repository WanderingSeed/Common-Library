package com.morgan.library.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

/**
 * 提供图片操作相关的实用方法。
 * 
 * @author Morgan.Ji
 * 
 */
public class ImageUtils {

    /**
     * 添加倒影，原理，先翻转图片，由上到下放大透明度
     * 
     * @param originalImage
     * @return
     */
    public static Bitmap createReflectedImage(Bitmap originalImage) {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 4;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * drawable 类型转化为bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 图片转圆形
     * 
     * @param bit
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return output;
    }

    /**
     * 图片水印生成的方法
     * 
     * @param src
     *            源图片位图
     * @param watermark
     *            水印图片位图
     * @return 返回一个加了水印的图片
     */
    public static Bitmap addWaterMark(Bitmap src, Bitmap watermark) {
        if (src == null)
            return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        // 初始化画布
        Canvas cv = new Canvas(newb);
        // 在 0，0坐标开始画入src
        cv.drawBitmap(src, 0, 0, null);
        // 在src的右下角画入水印
        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);
        // 保存，用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
        cv.save(Canvas.ALL_SAVE_FLAG);
        // 存储，用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
        cv.restore();
        return newb;
    }

    /**
     * 把位图变成圆角位图
     * 
     * @param bitmap
     *            需要修改的位图
     * @param pixels
     *            圆角的弧度
     * @return 圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    /**
     * 从二进制数据中得到位图
     * 
     * @param byteArray
     *            二进制数据
     * @return 位图
     */
    public static Bitmap byteToBitmap(byte[] byteArray) {
        if (byteArray.length != 0) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    /**
     * 从二进制数据中得到Drawable对象
     * 
     * @param byteArray
     *            二进制数据
     * @return Drawable对象
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
        return Drawable.createFromStream(ins, null);
    }

    /**
     * 把位图转换称二进制数据
     * 
     * @param bm
     *            位图
     * @return 二进制数据
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 以顺序方式链接两张图片
     * 
     * @param first
     * @param second
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap first, Bitmap second) {
        if (first == null || first.isRecycled() || second == null || second.isRecycled()) {
            return null;
        }
        int fWidth = first.getWidth();
        int fHeight = first.getHeight();
        int sHeight = second.getHeight();
        // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(fWidth, fHeight + sHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // 在 0，0坐标开始画入bg
        cv.drawBitmap(first, 0, 0, null);
        // 在 0，0坐标开始画入fg ，可以从任意位置画入
        cv.drawBitmap(second, 0, fHeight, null);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newbmp;
    }

    /**
     * 把一个图片放到另一个图片的中间，
     * 
     * @param bg
     *            背景图
     * @param bitmap
     *            上层的图片
     * @return
     */
    public static Bitmap componentBitmap(Bitmap bg, Bitmap bitmap) {
        int fWidth = bg.getWidth();
        int fHeight = bg.getHeight();
        int sWidth = bitmap.getWidth();
        int sHeight = bitmap.getHeight();
        // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(fWidth, fHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // 在 0，0坐标开始画入bg
        cv.drawBitmap(bg, 0, 0, null);
        // 在0，0坐标开始画入fg，可以从任意位置画入
        cv.drawBitmap(bitmap, (fWidth - sWidth) / 2, (fHeight - sHeight) / 2, null);
        // 保存
        cv.save(Canvas.ALL_SAVE_FLAG);
        // store
        cv.restore();// 存储
        return newbmp;
    }
}
