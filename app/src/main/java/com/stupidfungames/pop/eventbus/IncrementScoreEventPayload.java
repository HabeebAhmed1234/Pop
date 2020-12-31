package com.stupidfungames.pop.eventbus;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;

public class IncrementScoreEventPayload implements EventPayload {

  public final int incrementAmount;

  // Streak related data
  public final BubbleType poppedBubbleType;
  public final boolean isPoppedByTouch;

  public IncrementScoreEventPayload(int incrementAmount, BubbleType poppedBubbleType,
      boolean isPoppedByTouch) {
    this.incrementAmount = incrementAmount;
    this.poppedBubbleType = poppedBubbleType;
    this.isPoppedByTouch = isPoppedByTouch;
  }
}
