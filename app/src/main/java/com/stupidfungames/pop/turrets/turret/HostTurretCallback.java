package com.stupidfungames.pop.turrets.turret;

import org.andengine.entity.sprite.Sprite;

/**
 * Callback for getting information about the host turret of various turret entities.
 */
public interface HostTurretCallback {
    boolean setTurretAngle(float angle);
    Sprite getTurretBodySprite();
    Sprite getTurretCannonSprite();
    void setTurretPositionCenter(float x, float y);
}
