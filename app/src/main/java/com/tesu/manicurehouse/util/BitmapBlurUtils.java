package com.tesu.manicurehouse.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

/**
 * 毛玻璃处理类
 *
 * @author ycf
 */
public class BitmapBlurUtils {
    @SuppressLint("NewApi")
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
        if (VERSION.SDK_INT > 16) // 判断SDK的版本，可以更换
        {
            Bitmap bitmap = null;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            }


            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs,
                    sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs,
                    input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
                    Element.U8_4(rs));
            script.setRadius(radius);/* e.g. 3.f */
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 把原图变成灰色
     */
    public static Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 水平方向模糊度
     */
    private static float hRadius = 10;
    /**
     * 竖直方向模糊度
     */
    private static float vRadius = 10;
    /**
     * 模糊迭代度
     */
    private static int iterations = 7;

    /**
     * 高斯模糊
     */
    public static Drawable
    BoxBlurFilter(Bitmap bmp) {
        int width
                = bmp.getWidth();
        int height
                = bmp.getHeight();
        int[]
                inPixels = new int[width
                * height];
        int[]
                outPixels = new int[width
                * height];
        Bitmap
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);
        for (int i
             = 0;
             i < iterations; i++) {
            blur(inPixels,
                    outPixels, width, height, hRadius);
            blur(outPixels,
                    inPixels, height, width, vRadius);
        }
        blurFractional(inPixels,
                outPixels, width, height, hRadius);
        blurFractional(outPixels,
                inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels,
                0,
                width, 0,
                0,
                width, height);
        Drawable
                drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static void blur(int[]
                                    in, int[]
                                    out, int width,
                            int height,
                            float radius) {
        int widthMinus1
                = width - 1;
        int r
                = (int)
                radius;
        int tableSize
                = 2 *
                r + 1;
        int divide[]
                = new int[256 *
                tableSize];

        for (int i
             = 0;
             i < 256 *
                     tableSize; i++)
            divide[i]
                    = i / tableSize;

        int inIndex
                = 0;

        for (int y
             = 0;
             y < height; y++) {
            int outIndex
                    = y;
            int ta
                    = 0,
                    tr = 0,
                    tg = 0,
                    tb = 0;

            for (int i
                 = -r; i <= r; i++) {
                int rgb
                        = in[inIndex + clamp(i, 0,
                        width - 1)];
                ta
                        += (rgb >> 24)
                        & 0xff;
                tr
                        += (rgb >> 16)
                        & 0xff;
                tg
                        += (rgb >> 8)
                        & 0xff;
                tb
                        += rgb & 0xff;
            }

            for (int x
                 = 0;
                 x < width; x++) {
                out[outIndex]
                        = (divide[ta] << 24)
                        | (divide[tr] << 16)
                        |
                        (divide[tg] << 8)
                        | divide[tb];

                int i1
                        = x + r + 1;
                if (i1
                        > widthMinus1)
                    i1
                            = widthMinus1;
                int i2
                        = x - r;
                if (i2
                        < 0)
                    i2
                            = 0;
                int rgb1
                        = in[inIndex + i1];
                int rgb2
                        = in[inIndex + i2];

                ta
                        += ((rgb1 >> 24)
                        & 0xff)
                        - ((rgb2 >> 24)
                        & 0xff);
                tr
                        += ((rgb1 & 0xff0000)
                        - (rgb2 & 0xff0000))
                        >> 16;
                tg
                        += ((rgb1 & 0xff00)
                        - (rgb2 & 0xff00))
                        >> 8;
                tb
                        += (rgb1 & 0xff)
                        - (rgb2 & 0xff);
                outIndex
                        += height;
            }
            inIndex
                    += width;
        }
    }

    public static void blurFractional(int[]
                                              in, int[]
                                              out, int width,
                                      int height,
                                      float radius) {
        radius
                -= (int)
                radius;
        float f
                = 1.0f
                / (1 +
                2 *
                        radius);
        int inIndex
                = 0;

        for (int y
             = 0;
             y < height; y++) {
            int outIndex
                    = y;

            out[outIndex]
                    = in[0];
            outIndex
                    += height;
            for (int x
                 = 1;
                 x < width - 1;
                 x++) {
                int i
                        = inIndex + x;
                int rgb1
                        = in[i - 1];
                int rgb2
                        = in[i];
                int rgb3
                        = in[i + 1];

                int a1
                        = (rgb1 >> 24)
                        & 0xff;
                int r1
                        = (rgb1 >> 16)
                        & 0xff;
                int g1
                        = (rgb1 >> 8)
                        & 0xff;
                int b1
                        = rgb1 & 0xff;
                int a2
                        = (rgb2 >> 24)
                        & 0xff;
                int r2
                        = (rgb2 >> 16)
                        & 0xff;
                int g2
                        = (rgb2 >> 8)
                        & 0xff;
                int b2
                        = rgb2 & 0xff;
                int a3
                        = (rgb3 >> 24)
                        & 0xff;
                int r3
                        = (rgb3 >> 16)
                        & 0xff;
                int g3
                        = (rgb3 >> 8)
                        & 0xff;
                int b3
                        = rgb3 & 0xff;
                a1
                        = a2 + (int)
                        ((a1 + a3) * radius);
                r1
                        = r2 + (int)
                        ((r1 + r3) * radius);
                g1
                        = g2 + (int)
                        ((g1 + g3) * radius);
                b1
                        = b2 + (int)
                        ((b1 + b3) * radius);
                a1
                        *= f;
                r1
                        *= f;
                g1
                        *= f;
                b1
                        *= f;
                out[outIndex]
                        = (a1 << 24)
                        | (r1 << 16)
                        | (g1 << 8)
                        | b1;
                outIndex
                        += height;
            }
            out[outIndex]
                    = in[width - 1];
            inIndex
                    += width;
        }
    }

    public static int clamp(int x,
                            int a,
                            int b) {
        return (x
                < a) ? a : (x > b) ? b : x;
    }

}
