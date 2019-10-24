package com.wack.pop2;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

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

    public static void init(Activity activity) {
        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13 ) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            sScreenSize = new ScreenSize(display.getWidth(), display.getHeight());
        } else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            sScreenSize = new ScreenSize(size.x, size.y);
        }
    }

    public static ScreenSize getSreenSize() {
        if (sScreenSize == null) {
            throw new IllegalStateException("Cannot get screen size without ScreenUtils.init being called");
        }
        return sScreenSize;
    }
}
