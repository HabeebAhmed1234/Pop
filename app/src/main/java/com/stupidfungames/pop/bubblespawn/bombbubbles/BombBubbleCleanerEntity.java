package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.BaseCleanerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.entitymatchers.BombBubbleEntityMatcher;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

public class BombBubbleCleanerEntity extends BaseCleanerEntity {

  public BombBubbleCleanerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected IEntityMatcher getEntityMatcherForEntityToClean() {
    return new BombBubbleEntityMatcher();
  }

  @Override
  protected void clean(IEntity entity) {
    if (entity.isVisible()) {
      get(BombBubbleSpritePool.class).recycle(entity);
    }
  }
}
