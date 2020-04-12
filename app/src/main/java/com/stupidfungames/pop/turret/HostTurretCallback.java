package com.stupidfungames.pop.turret;

import org.andengine.entity.sprite.Sprite;

/**
 * Callback for getting information about the host turret of various turret entities.
 */
public interface HostTurretCallback {
    boolean setTurretAngle(float angle);
    Sprite getTurretBodySprite();
    Sprite getTurretCannonSprite();
    void setTurretPosition(float x, float y);
}