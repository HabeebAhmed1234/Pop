package com.stupidfungames.pop.fixturedefdata;

import org.andengine.entity.sprite.Sprite;

public class WallEntityUserData extends BaseEntityUserData {

    public Sprite wallDeleteIcon;

    @Override
    public void reset() {
        super.reset();
        wallDeleteIcon = null;
    }
}
