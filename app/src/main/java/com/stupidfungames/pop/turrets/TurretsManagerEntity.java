package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntityCreator;
import com.stupidfungames.pop.turrets.turret.TurretBulletSpritePool;
import com.stupidfungames.pop.utils.Mutex;

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
    binder.bind(Mutex.class, new Mutex());
    binder.bind(BulletExplosionsEffectEntity.class, new BulletExplosionsEffectEntity(this));
    binder.bind(BaseDraggableEntityCreator.class, new TurretEntityCreator(this));
    binder.bind(TurretsBulletsCollisionManager.class, new TurretsBulletsCollisionManager(this));
    binder.bind(TurretsInventoryIconEntity.class, new TurretsInventoryIconEntity(this));
  }
}
