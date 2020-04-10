package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.fixturedefdata.WallEntityUserData;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

import androidx.annotation.Nullable;

public class WallsEntityMatcher implements IEntityMatcher {

    @Override
    public boolean matches(IEntity pEntity) {
        @Nullable Object userdata = pEntity.getUserData();
        return userdata != null && userdata instanceof WallEntityUserData;
    }
}
