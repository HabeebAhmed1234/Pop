package com.wack.pop2.entitymatchers;

import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.shape.IShape;

import androidx.annotation.Nullable;

public class BubblesEntityMatcher implements IEntityMatcher {

    private final boolean excludeTargetedBubbles;
    private final boolean onlyPoppableBubbles;

    public BubblesEntityMatcher(boolean excludeTargetedBubbles, boolean onlyPoppableBubbles) {
        this.excludeTargetedBubbles = excludeTargetedBubbles;
        this.onlyPoppableBubbles = onlyPoppableBubbles;
    }

    @Override
    public boolean matches(IEntity pEntity) {
        @Nullable Object userdata = pEntity.getUserData();
        if (userdata != null && userdata instanceof BubbleEntityUserData) {
            BubbleEntityUserData bubbleUserData = (BubbleEntityUserData) userdata;
            if (excludeTargetedBubbles && bubbleUserData.isTargeted) {
                return false;
            }
            return onlyPoppableBubbles ? (bubbleUserData.isPoppable() && isBubbleInPoppableBounds(pEntity)) : true;
        }
        return false;
    }

    private boolean isBubbleInPoppableBounds(IEntity entity) {
        IShape shape = (IShape) entity;
        return ScreenUtils.isInScreen(shape);
    }
}