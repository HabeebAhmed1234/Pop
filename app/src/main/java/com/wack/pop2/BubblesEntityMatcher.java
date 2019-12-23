package com.wack.pop2;

import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

import androidx.annotation.Nullable;

public class BubblesEntityMatcher implements IEntityMatcher {

    @Override
    public boolean matches(IEntity pEntity) {
        @Nullable Object userdata = pEntity.getUserData();
        if (userdata != null) {
            return userdata instanceof BubbleEntityUserData;
        }
        return false;
    }
}
