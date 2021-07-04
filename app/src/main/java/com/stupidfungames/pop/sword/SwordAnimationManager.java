package com.stupidfungames.pop.sword;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.sword.SwordLineSpritesPool.LineSpriteInitializerParams;
import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.input.touch.TouchEvent;

public class SwordAnimationManager extends BaseEntity {

  private static final float MIN_LINE_SIZE_PX = 20;

  private final SwordLineSpritesPool swordLineSpritesPool;
  private float prevX = -1;
  private float prevY = -1;

  public SwordAnimationManager(BinderEnity parent) {
    super(parent);
    this.swordLineSpritesPool = new SwordLineSpritesPool(this);
  }

  public void onTouchEvent(TouchEvent touchEvent) {
    if (prevX == -1 || prevY == -1) {
      prevX = touchEvent.getX();
      prevY = touchEvent.getY();
    }
    float touchX = touchEvent.getX();
    float touchY = touchEvent.getY();
    if (GeometryUtils.distanceBetween(prevX, prevY, touchX, touchY) > MIN_LINE_SIZE_PX) {
      // Draw a line between previous point and current touch point.
      addToScene(swordLineSpritesPool.get(new LineSpriteInitializerParams(prevX, prevY, touchX, touchY)));
      prevX = touchEvent.getX();
      prevY = touchEvent.getY();
    }
  }
}
