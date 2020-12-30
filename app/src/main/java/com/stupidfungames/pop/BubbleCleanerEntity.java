package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

/**
 * Sometimes bubbles slip through the physics floor we have in the game so we can make this cleaner
 * to help catch those exceptions.
 */
public class BubbleCleanerEntity extends BaseCleanerEntity {

  public BubbleCleanerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected IEntityMatcher getEntityMatcherForEntityToClean() {
    return new BubblesEntityMatcher(false, false);
  }

  @Override
  protected void clean(IEntity entity) {
    if (entity.isVisible()) {
      get(BubbleSpritePool.class).recycle(entity);
    }
  }
}
