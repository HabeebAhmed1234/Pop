package com.stupidfungames.pop.eventbus;

public class WallV2PoppedBubbleEventPayload implements EventPayload {

  public final int wallId;

  public WallV2PoppedBubbleEventPayload(int wallId) {
    this.wallId = wallId;
  }
}
