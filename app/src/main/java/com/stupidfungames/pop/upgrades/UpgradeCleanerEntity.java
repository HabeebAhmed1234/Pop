package com.stupidfungames.pop.upgrades;

import com.stupidfungames.pop.BaseCleanerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.entitymatchers.UpgradeEntityMatcher;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

public class UpgradeCleanerEntity extends BaseCleanerEntity {

  public UpgradeCleanerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected IEntityMatcher getEntityMatcherForEntityToClean() {
    return new UpgradeEntityMatcher();
  }

  @Override
  protected void clean(IEntity entity) {
    removeFromScene(entity);
  }
}
