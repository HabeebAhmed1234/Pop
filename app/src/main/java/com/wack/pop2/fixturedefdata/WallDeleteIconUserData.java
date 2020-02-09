package com.wack.pop2.fixturedefdata;

import org.andengine.entity.shape.IShape;
import org.jbox2d.dynamics.Body;

public class WallDeleteIconUserData extends BaseEntityUserData {
    public final IShape wallSprite;
    public final Body wallBody;

    public WallDeleteIconUserData(IShape wallSprite, Body wallBody) {
        this.wallSprite = wallSprite;
        this.wallBody = wallBody;
    }
}