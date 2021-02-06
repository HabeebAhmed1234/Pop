package com.stupidfungames.pop.utils;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import androidx.core.util.Preconditions;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScreenUtils {

  public static class ScreenSize {

    public final int widthPx;
    public final int heightPx;
    public final int safeInsetTopHeightPx;

    public ScreenSize(int widthPx, int heightPx, int safeInsetTopHeightPx) {
      this.widthPx = widthPx;
      this.heightPx = heightPx;
      this.safeInsetTopHeightPx = safeInsetTopHeightPx;
    }
  }

  /**
   * Standard overlap that a sprite should have with the screen to be considered "in the screen".
   */
  public static final float PERCENT_SPRITE_IN_SCREEN = 1 / 2f;
  public static final float FIXED_GAME_SCREEN_WIDTH = 1440;

  private static DisplayMetrics sDisplayMetrics;
  private static ScreenSize sScreenSize;
  private static RectF sScreenRect;
  private static IAreaShape sScreenShape;

  /**
   * Initializes when initialize is called
   */
  public static void initialize(Activity activity) {
    sDisplayMetrics = activity.getResources().getDisplayMetrics();

    float screenWidthPx = 0;
    float screenHeightPx = 0;
    float safeInsetTopHeightPx = 0;

    if (Integer.valueOf(VERSION.SDK_INT) < 13) {
      Display display = activity.getWindowManager().getDefaultDisplay();
      screenWidthPx = display.getWidth();
      screenHeightPx = display.getHeight();
    } else {
      Point size = getAppWindowSize(activity);

      screenWidthPx = size.x;
      screenHeightPx = size.y;

      if (VERSION.SDK_INT >= VERSION_CODES.Q) {
        DisplayCutout displayCutout = activity.getWindowManager().getDefaultDisplay().getCutout();
        if (displayCutout != null) {
          screenHeightPx += displayCutout.getSafeInsetTop() + displayCutout.getSafeInsetBottom();
          safeInsetTopHeightPx = displayCutout.getSafeInsetTop();
        }
      }
    }

    int gameScreenHeight = (int) (FIXED_GAME_SCREEN_WIDTH / screenWidthPx * screenHeightPx);
    float phoneScreenToGameScreenHeightRatio = gameScreenHeight / screenHeightPx;
    sScreenSize = new ScreenSize(
        (int) FIXED_GAME_SCREEN_WIDTH,
        gameScreenHeight,
        (int) (safeInsetTopHeightPx * phoneScreenToGameScreenHeightRatio));
    sScreenRect = new RectF(0, 0, sScreenSize.widthPx, sScreenSize.heightPx);
  }

  /**
   * Initializes when onCreateResources is called
   */
  public static void onCreateResources(VertexBufferObjectManager vertexBufferObjectManager) {
    if (sScreenShape == null) {
      sScreenShape = new Rectangle(0, 0, getSreenSize().widthPx, getSreenSize().heightPx,
          vertexBufferObjectManager);
    }
  }

  public static ScreenSize getSreenSize() {
    if (sScreenSize == null) {
      throw new IllegalStateException(
          "Cannot get screen size without ScreenUtils.initialize being called");
    }
    return sScreenSize;
  }

  public static RectF getScreenRect() {
    if (sScreenRect == null) {
      throw new IllegalStateException(
          "Cannot get screen rect without ScreenUtils.initialize being called");
    }
    return sScreenRect;
  }

  public static boolean isInScreen(IAreaShape shape) {
    return isInScreen(shape, false);
  }

  public static boolean isInScreen(IAreaShape shape, boolean isFullyInScreen) {
    return shape.isVisible() && (isFullyInScreen ? GeometryUtils.isShapeInShape(sScreenShape, shape)
        : shape.collidesWith(sScreenShape));
  }

  /**
   * Returns true if at least percent of the shape is in the screen
   */
  public static boolean isInScreen(IAreaShape shape, float percent) {
    Preconditions.checkArgument(percent >= 0 && percent <= 1);
    if (!shape.isVisible()) {
      return false;
    }
    float overlapPercent = (GeometryUtils.getOverLapAreaWithScreen(shape, sScreenShape) / (
        shape.getWidthScaled() * shape.getHeightScaled()));
    return overlapPercent >= percent;
  }

  public static boolean isInScreen(float x, float y) {
    return sScreenRect.contains(x, y);
  }

  public static int pxToDp(int pX) {
    return (int) Math.ceil(pX / sDisplayMetrics.density);
  }

  private static Point getAppWindowSize(Activity activity) {
    Point point = new Point();
    activity.getWindowManager().getDefaultDisplay().getSize(point);
    return point;
  }
}
