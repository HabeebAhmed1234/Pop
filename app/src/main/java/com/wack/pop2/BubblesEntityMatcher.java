package com.wack.pop2;

import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

import androidx.annotation.Nullable;

public class BubblesEntityMatcher implements IEntityMatcher {

    private final boolean onlyPoppableBubbles;

    public BubblesEntityMatcher(boolean onlyPoppableBubbles) {
        this.onlyPoppableBubbles = onlyPoppableBubbles;
    }

    @Override
    public boolean matches(IEntity pEntity) {
        @Nullable Object userdata = pEntity.getUserData();
        if (userdata != null && userdata instanceof BubbleEntityUserData) {
            BubbleEntityUserData bubbleUserData = (BubbleEntityUserData) userdata;
            return onlyPoppableBubbles ? bubbleUserData.isPoppable() : true;
        }
        return false;
    }
}
