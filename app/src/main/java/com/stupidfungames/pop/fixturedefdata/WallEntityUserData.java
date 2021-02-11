package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.WALL;

import org.andengine.entity.sprite.Sprite;

public class WallEntityUserData extends BaseEntityUserData {

    public Sprite wallDeleteIcon;

    @Override
    public void reset() {
        super.reset();
        wallDeleteIcon = null;
    }

    @Override
    public int collisionType() {
        return WALL;
    }
}
