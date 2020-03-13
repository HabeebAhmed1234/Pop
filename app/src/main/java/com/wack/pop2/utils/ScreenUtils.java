package com.wack.pop2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.Display;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IShape;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScreenUtils {

    public static class ScreenSize {
        public final int widthPx;
        public final int heightPx;
        public final int widthDp;
        public final int heightDp;

        public ScreenSize(int widthPx, int heightPx, int widthDp, int heightDp) {
            this.widthPx = widthPx;
            this.heightPx = heightPx;
            this.widthDp = widthDp;
            this.heightDp = heightDp;
        }
    }

    private static ScreenSize sScreenSize;
    private static RectF sScreenRect;
    private static IShape sScreenShape;

    /**
     * Initializes when onCreateEngineOptions is called
     */
    public static void onCreateEngineOptions(Activity activity) {
        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13 ) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            sScreenSize = new ScreenSize(display.getWidth(), display.getHeight(), pxToDp(display.getWidth(), activity), pxToDp(display.getHeight(), activity));
        } else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            sScreenSize = new ScreenSize(size.x, size.y, pxToDp(size.x, activity), pxToDp(size.y, activity));
        }

        sScreenRect = new RectF(0, 0, sScreenSize.widthPx, sScreenSize.heightPx);
    }

    /**
     * Initializes when onCreateResources is called
     */
    public static void onCreateResources(VertexBufferObjectManager vertexBufferObjectManager) {
        if (sScreenShape == null) {
            sScreenShape = new Rectangle(0, 0, getSreenSize().widthPx, getSreenSize().heightPx, vertexBufferObjectManager);
        }
    }

    public static ScreenSize getSreenSize() {
        if (sScreenSize == null) {
            throw new IllegalStateException("Cannot get screen size without ScreenUtils.onCreateEngineOptions being called");
        }
        return sScreenSize;
    }

    public static RectF getScreenRect() {
        if (sScreenRect == null) {
            throw new IllegalStateException("Cannot get screen rect without ScreenUtils.onCreateEngineOptions being called");
        }
        return sScreenRect;
    }

    public static boolean isInScreen(IShape shape) {
        return shape.collidesWith(sScreenShape);
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int pxToDp(int pX, Context context) {
        return (int) Math.ceil(pX / context.getResources().getDisplayMetrics().density);
    }
}
