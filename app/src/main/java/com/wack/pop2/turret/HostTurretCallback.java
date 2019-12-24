package com.wack.pop2.turret;

import org.andengine.entity.sprite.Sprite;

/**
 * Callback for getting information about the host turret of various turret entities.
 */
public interface HostTurretCallback {
    void setTurretAngle(float angle);
    Sprite getTurretBodySprite();
    Sprite getTurretCannonSprite();
}
