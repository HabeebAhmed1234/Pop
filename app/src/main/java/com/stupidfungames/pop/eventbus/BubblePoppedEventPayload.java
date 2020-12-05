package com.stupidfungames.pop.eventbus;

public class BubblePoppedEventPayload implements EventPayload {

  public final int bubbleId;
  public final float poppedBubbleX;
  public final float poppedBubbleY;

  public BubblePoppedEventPayload(int bubbleId, float poppedBubbleX, float poppedBubbleY) {
    this.bubbleId = bubbleId;
    this.poppedBubbleX = poppedBubbleX;
    this.poppedBubbleY = poppedBubbleY;
  }
}
