package com.wack.pop2.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.Display;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IShape;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScreenUtils {

    public static class ScreenSize {
        public final int width;
        public final int height;

        public ScreenSize(int width, int height) {
            this.width = width;
            this.height = height;
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
            sScreenSize = new ScreenSize(display.getWidth(), display.getHeight());
        } else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            sScreenSize = new ScreenSize(size.x, size.y);
        }

        sScreenRect = new RectF(0, 0, sScreenSize.width, sScreenSize.height);
    }

    /**
     * Initializes when onCreateResources is called
     */
    public static void onCreateResources(VertexBufferObjectManager vertexBufferObjectManager) {
        if (sScreenShape == null) {
            sScreenShape = new Rectangle(0, 0, getSreenSize().width, getSreenSize().height, vertexBufferObjectManager);
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
}
