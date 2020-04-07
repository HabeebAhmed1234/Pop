package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity {

    public TurretsManagerEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(TurretsMutex.class, new TurretsMutex(this));
        binder.bind(BulletExplosionsBaseEntity.class, new BulletExplosionsBaseEntity(this));
        binder.bind(TurretEntityCreator.class, new TurretEntityCreator(this));
        binder.bind(TurretsBulletsCollisionManager.class, new TurretsBulletsCollisionManager(this));
        binder.bind(TurretsIconEntity.class, new TurretsIconEntity(this));
    }
}
