package com.wack.pop2.utils;

import com.wack.pop2.physics.util.Vec2Pool;

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
}
