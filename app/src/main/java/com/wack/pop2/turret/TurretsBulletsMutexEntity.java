package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.entity.sprite.Sprite;

/**
 * All bullets in flight are tracked here. This entity can be called to ensure that no other bullets
 * are already targeting a given target.
 */
public class TurretsBulletsMutexEntity extends BaseEntity {


    public TurretsBulletsMutexEntity(GameResources gameResources) {
        super(gameResources);
    }

    public void onBulletTargeted(TurretBulletEntity bulletEntity, Sprite target) {

    }

    public boolean isAlreadyTargeted(Sprite targetBubble) {
        // TODO implement this
        return false;
    }
}
