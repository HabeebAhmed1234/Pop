package com.stupidfungames.pop.sword;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.sword.SwordLineSpritesPool.LineSpriteInitializerParams;
import com.stupidfungames.pop.sword.SwordStateMachine.State;
import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public class SwordAnimationManager extends BaseEntity {

  public static final float MIN_LINE_SIZE_PX = 20;

  private final SwordLineSpritesPool swordLineSpritesPool;
  private SwordStateMachine swordStateMachine;
  private float prevX = -1;
  private float prevY = -1;

  public SwordAnimationManager(BinderEnity parent) {
    super(parent);
    this.swordLineSpritesPool = new SwordLineSpritesPool(this);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    this.swordStateMachine = get(SwordStateMachine.class);
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
      addToScene(
          swordLineSpritesPool
              .get(new LineSpriteInitializerParams(prevX, prevY, touchX, touchY, getLineColor())));
      prevX = touchEvent.getX();
      prevY = touchEvent.getY();
    }
  }

  private AndengineColor getLineColor() {
    return swordStateMachine.getCurrentState() == State.CHARGED ? AndengineColor.GREEN
        : AndengineColor.RED;
  }

  public void clear() {
    prevX = -1;
    prevY = -1;
  }
}
