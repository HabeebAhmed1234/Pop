package com.stupidfungames.pop.eventbus;

public class BubblePoppedEventPayload implements EventPayload {

  public final int bubbleId;

  public BubblePoppedEventPayload(int bubbleId) {
    this.bubbleId = bubbleId;
  }
}
