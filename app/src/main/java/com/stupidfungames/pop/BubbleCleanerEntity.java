package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.List;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;

/**
 * Sometimes bubbles slip through the physics floor we have in the game so we can make this cleaner
 * to help catch those exceptions.
 */
public class BubbleCleanerEntity extends BaseEntity {

  private static final float BUBBLE_OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS = 1f;

  public BubbleCleanerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {

  }

  @Override
  public void onCreateScene() {
    engine.registerUpdateHandler(
        new TimerHandler(
            BUBBLE_OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS,
            true,
            new ITimerCallback() {
              @Override
              public void onTimePassed(TimerHandler pTimerHandler) {
                cleanUpBubbles();
              }
            }));
  }

  private void cleanUpBubbles() {
    List<IEntity> bubbles = getAllBubbles();
    for (int i = 0; i < bubbles.size(); i++) {
      IEntity bubble = bubbles.get(i);
      if (bubble.getY() > ScreenUtils.getSreenSize().heightPx * 1.2) {
        if (bubble instanceof IShape) {
          get(BubbleSpritePool.class).recycle(bubble);
        }
      }
    }
  }

  /**
   * Returns all the bubbles present in the scene
   */
  private List<IEntity> getAllBubbles() {
    return scene.query(new BubblesEntityMatcher(false, false));
  }
}
