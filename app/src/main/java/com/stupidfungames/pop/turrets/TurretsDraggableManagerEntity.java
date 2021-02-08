package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntityCreator;
import com.stupidfungames.pop.draggableinventory.BaseDraggableInventoryIcon;
import com.stupidfungames.pop.draggableinventory.BaseDraggableManagerEntity;
import com.stupidfungames.pop.turrets.turret.TurretBulletSpritePool;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsDraggableManagerEntity extends BaseDraggableManagerEntity {

  public TurretsDraggableManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(TurretBulletSpritePool.class, new TurretBulletSpritePool(this))
        .bind(BulletExplosionsEffectEntity.class, new BulletExplosionsEffectEntity(this))
        .bind(TurretsBulletsCollisionManager.class, new TurretsBulletsCollisionManager(this));
  }

  @Override
  protected BaseDraggableEntityCreator getDraggableEntityCreator() {
    return new TurretDraggableEntityCreator(this);
  }

  @Override
  protected BaseDraggableInventoryIcon getDraggableInventoryIcon() {
    return new TurretsInventoryIconEntity(this);
  }
}
