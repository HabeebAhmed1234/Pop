package com.stupidfungames.pop.entitymatchers;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.shape.IAreaShape;

public class BubblesEntityMatcher implements IEntityMatcher {

  private final boolean excludeTargetedBubbles;
  private final boolean onlyPoppableBubbles;

  public BubblesEntityMatcher(boolean excludeTargetedBubbles, boolean onlyPoppableBubbles) {
    this.excludeTargetedBubbles = excludeTargetedBubbles;
    this.onlyPoppableBubbles = onlyPoppableBubbles;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    if (!pEntity.isVisible()) {
      return false;
    }
    @Nullable Object userdata = pEntity.getUserData();
    if (userdata != null && userdata instanceof BubbleEntityUserData) {
      BubbleEntityUserData bubbleUserData = (BubbleEntityUserData) userdata;
      if (excludeTargetedBubbles && bubbleUserData.isTargeted) {
        return false;
      }
      return onlyPoppableBubbles ? isBubbleInPoppableBounds(pEntity) : true;
    }
    return false;
  }

  private boolean isBubbleInPoppableBounds(IEntity entity) {
    return ScreenUtils.isInScreen((IAreaShape) entity);
  }
}
