package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import org.andengine.entity.IEntity;

public class SameBubblesTypeEntityMatcher extends BubblesEntityMatcher {

  private final BubbleType bubbleType;

  public SameBubblesTypeEntityMatcher(boolean excludeTargetedBubbles,
      boolean onlyPoppableBubbles, BubbleType bubbleType) {
    super(excludeTargetedBubbles, onlyPoppableBubbles);
    this.bubbleType = bubbleType;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    Object userData = pEntity.getUserData();
    if (userData instanceof BubbleEntityUserData) {
      BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) userData;
      return bubbleEntityUserData.bubbleType == bubbleType && super.matches(pEntity);
    }
    return false;
  }
}
