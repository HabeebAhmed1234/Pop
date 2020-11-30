package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;

public class SameBubblesTypeEntityMatcher extends BubblesEntityMatcher {

  private final BubbleType bubbleType;

  public SameBubblesTypeEntityMatcher(boolean excludeTargetedBubbles,
      boolean onlyPoppableBubbles, BubbleType bubbleType) {
    super(excludeTargetedBubbles, onlyPoppableBubbles);
    this.bubbleType = bubbleType;
  }

  
}
