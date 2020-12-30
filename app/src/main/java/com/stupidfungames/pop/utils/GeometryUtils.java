package com.stupidfungames.pop.utils;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import org.andengine.entity.IEntity;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.jbox2d.common.Vec2;

public class GeometryUtils {

  public static float getAngleOfCenters(Sprite s1, Sprite s2) {
    Vec2 s1C = getCenter(s1);
    Vec2 s2C = getCenter(s2);
    float angle = getAngle(s1C.x, s1C.y, s2C.x, s2C.y);
    Vec2Pool.recycle(s1C);
    Vec2Pool.recycle(s2C);
    return angle;
  }

  public static float getAngle(Vec2 vec2) {
    return getAngle(0, 0, vec2.x, vec2.y);
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
    return Vec2Pool.obtain(sprite.getX() + sprite.getWidthScaled() / 2,
        sprite.getY() + sprite.getHeightScaled() / 2);
  }

  public static float distanceBetween(IEntity e1, IEntity e2) {
    return distanceBetween(e1.getX(), e1.getY(), e2.getX(), e2.getY());
  }

  /**
   * Returns the distance between the edges of two circles.
   */
  public static float distanceBetween(float x1, float y1, float x2, float y2, float r1, float r2) {
    return (float) (sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2)) - (r2 + r1));
  }

  public static float distanceBetween(float x1, float y1, float x2, float y2) {
    return (float) sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  public static float[] getCenterPoint(float x1, float y1, float x2, float y2) {
    return new float[]{x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2};
  }

  /**
   * Scales the given sprite to fix the screen widthPx and heightPx while maintaining the aspect
   * ratio
   */
  public static void scaleToFixScreen(Sprite srcSprite) {
    Matrix matrix = new Matrix();
    RectF dstRect = new RectF(ScreenUtils.getScreenRect());
    matrix.setRectToRect(new RectF(0, 0, (int) srcSprite.getWidth(), (int) srcSprite.getHeight()),
        dstRect, Matrix.ScaleToFit.CENTER);
    srcSprite.setWidth(dstRect.width());
    srcSprite.setHeight(dstRect.height());
  }

  public static void initSpriteDimensCenterPos(
      Context context,
      Sprite sprite,
      float centerPosX,
      float centerPosY,
      int sizeDp) {
    initSpriteDimensCenterPos(sprite, centerPosX, centerPosY, ScreenUtils.dpToPx(sizeDp, context));

  }

  public static void initSpriteDimensCenterPos(
      Sprite sprite,
      float centerPosXPx,
      float centerPosYPx,
      int sizePx) {
    initSpriteDimens(sprite, centerPosXPx - sizePx / 2, centerPosYPx - sizePx / 2, sizePx, sizePx);
  }

  public static void initSpriteDimens(
      Sprite sprite,
      float posXPx,
      float posYPx,
      int widthPx,
      int heightPx) {
    sprite.setWidth(widthPx);
    sprite.setHeight(heightPx);
    sprite.setX(posXPx);
    sprite.setY(posYPx);
  }

  public static Vec2 getVector(float angle, float magnitude) {
    return Vec2Pool.obtain((float) sin(angle) * magnitude, (float) cos(angle) * magnitude);
  }

  public static float getMagnitude(float x, float y) {
    return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  public static Vec2 getVector(Vec2 point1, Vec2 point2, float magnitude) {
    float newX = point2.x - point1.x;
    float newY = point2.y - point1.y;
    float mag = getMagnitude(newX, newY);
    newX = newX * magnitude / mag;
    newY = newY * magnitude / mag;
    return Vec2Pool.obtain(newX, newY);
  }

  /**
   * Returns a new X position such that the object with position x and objectwidth won't appear off
   * screen.
   */
  public static float constrainXToScreenWidth(float x, float objectWidth) {
    if (x < 0) {
      x = 0;
    }
    if (x + objectWidth > ScreenUtils.getSreenSize().widthPx) {
      x = ScreenUtils.getSreenSize().widthPx - objectWidth;
    }
    return x;
  }

  /**
   * Returns a new Y position such that the object with position y and objectHeight won't appear off
   * screen.
   */
  public static float constrainYToScreenHeight(float y, float objectHeight) {
    if (y < 0) {
      y = 0;
    }
    if (y + objectHeight > ScreenUtils.getSreenSize().heightPx) {
      y = ScreenUtils.getSreenSize().heightPx - objectHeight;
    }
    return y;
  }

  public static void centerInHorizontal(IAreaShape parent, Text child) {
    child.setX(parent.getWidthScaled() / 2 - child.getLineWidthMaximum());
  }
}
