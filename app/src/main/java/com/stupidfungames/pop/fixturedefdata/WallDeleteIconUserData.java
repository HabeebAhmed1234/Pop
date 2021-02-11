package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.WALL_DELETE_ICON;

import org.andengine.entity.shape.IShape;
import org.jbox2d.dynamics.Body;

public class WallDeleteIconUserData extends BaseEntityUserData {
    public IShape wallSprite;
    public Body wallBody;

    public WallDeleteIconUserData(IShape wallSprite, Body wallBody) {
        this.wallSprite = wallSprite;
        this.wallBody = wallBody;
    }

    @Override
    public void reset() {
        super.reset();
        wallSprite = null;
        wallBody = null;
    }

    @Override
    public int collisionType() {
        return WALL_DELETE_ICON;
    }
}
