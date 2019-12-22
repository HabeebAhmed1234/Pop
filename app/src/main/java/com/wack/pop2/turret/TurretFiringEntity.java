package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.entity.IEntity;

/**
 * Responsible for actually firing a projectile at a bubble. When prompted to fire this transitions
 * the state machine to FIRING, fires the projectile, and transitions the state machine back to
 * TARGETING
 */
public class TurretFiringEntity extends BaseEntity {

    private boolean isReadyToFire = true;

    public TurretFiringEntity(GameResources gameResources) {
        super(gameResources);
    }

    public boolean isReadyToFire() {
        return isReadyToFire;
    }

    public void fire(IEntity target) {

    }
}
