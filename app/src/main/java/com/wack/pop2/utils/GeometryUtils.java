package com.wack.pop2.utils;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.constraintlayout.solver.widgets.Rectangle;

import com.wack.pop2.physics.util.Vec2Pool;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.common.Vec2;

public class GeometryUtils {

    public static float getAngleOfCenters(Sprite s1, Sprite s2) {
        Vec2 s1C = getCenter(s1);
        Vec2 s2C = getCenter(s2);
        return getAngle(s1C.x, s1C.y, s2C.x, s2C.y);
    }

    public static float getAngle(float x1, float y1, float x2, float y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double angle = Math.toDegrees(theta);
        if (angle < 0) {
            angle += 360;
        }
        return (float) angle;
    }

    public static Vec2 getCenter(Sprite sprite) {
        return Vec2Pool.obtain(sprite.getX() + sprite.getWidthScaled() / 2, sprite.getY() + sprite.getHeightScaled() / 2);
    }

    public static float distanceBetween(IEntity e1, IEntity e2) {
        return distanceBetween(e1.getX(), e1.getY(), e2.getX(), e2.getY());
    }

    public static float distanceBetween(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static float[] getCenterPoint(float x1, float y1, float x2, float y2) {
        return new float[]{ x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2};
    }

    /**
     * Scales the given sprite to fix the screen widthPx and heightPx while maintaining the aspect ratio
     * @return
     */
    public static void scaleToFixScreen(Sprite srcSprite) {
        Matrix matrix = new Matrix();
        RectF dstRect = new RectF(ScreenUtils.getScreenRect());
        matrix.setRectToRect(new RectF(0, 0, (int) srcSprite.getWidth(), (int) srcSprite.getHeight()), dstRect, Matrix.ScaleToFit.CENTER);
        srcSprite.setWidth(dstRect.width());
        srcSprite.setHeight(dstRect.height());
    }
}
