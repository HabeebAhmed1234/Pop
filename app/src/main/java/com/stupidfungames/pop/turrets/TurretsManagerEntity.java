package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.turrets.turret.TurretBulletSpritePool;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

  public TurretsManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(TurretBulletSpritePool.class, new TurretBulletSpritePool(this));
    binder.bind(TurretsMutex.class, new TurretsMutex(this));
    binder.bind(BulletExplosionsEntity.class, new BulletExplosionsEntity(this));
    binder.bind(TurretEntityCreator.class, new TurretEntityCreator(this));
    binder.bind(TurretsBulletsCollisionManager.class, new TurretsBulletsCollisionManager(this));
    binder.bind(TurretsInventoryIconEntity.class, new TurretsInventoryIconEntity(this));
  }
}
