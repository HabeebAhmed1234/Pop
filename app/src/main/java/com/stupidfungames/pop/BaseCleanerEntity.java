package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.List;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.shape.IShape;

/**
 * Sometimes sprites slip through the physics floor we have in the game so we can make this cleaner
 * to help catch those exceptions.
 */
public abstract class BaseCleanerEntity extends BaseEntity {

  private static final float OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS = 1f;

  private final TimerHandler checkerTimerHandler = new TimerHandler(
      OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS,
      true,
      new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          cleanUpEntities();
        }
      });

  public BaseCleanerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    engine.registerUpdateHandler(checkerTimerHandler);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    engine.unregisterUpdateHandler(checkerTimerHandler);
  }

  private void cleanUpEntities() {
    List<IEntity> entities = getAllEntities();
    for (int i = 0; i < entities.size(); i++) {
      IEntity entity = entities.get(i);
      if (entity.getY() > ScreenUtils.getSreenSize().heightPx * 1.2) {
        if (entity instanceof IShape) {
          clean(entity);
        }
      }
    }
  }

  /**
   * Returns all the bubbles present in the scene
   */
  private List<IEntity> getAllEntities() {
    return scene.query(getEntityMatcherForEntityToClean());
  }

  protected abstract IEntityMatcher getEntityMatcherForEntityToClean();

  /**
   * Called when this entity should be cleaned up.
   */
  protected abstract void clean(IEntity entity);

}
