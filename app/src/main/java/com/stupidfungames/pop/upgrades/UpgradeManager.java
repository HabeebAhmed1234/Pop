package com.stupidfungames.pop.upgrades;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

/**
 * Holds all the upgrade related entities
 */
public class UpgradeManager extends BaseEntity {

  public UpgradeManager(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(UpgradeInventory.class, new UpgradeInventory(this))
        .bind(UpgradeSpawner.class, new UpgradeSpawner(this))
        .bind(UpgradeLossDetector.class, new UpgradeLossDetector(this))
        .bind(UpgradeCleanerEntity.class, new UpgradeCleanerEntity(this));
  }
}
