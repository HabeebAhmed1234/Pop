package com.stupidfungames.pop.eventbus;

public class BubbleRecycledEventPayload implements EventPayload {

  public final int bubbleId;

  public BubbleRecycledEventPayload(int bubbleId) {
    this.bubbleId = bubbleId;
  }
}
