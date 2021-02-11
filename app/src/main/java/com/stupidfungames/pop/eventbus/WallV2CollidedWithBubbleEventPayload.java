package com.stupidfungames.pop.eventbus;

import org.andengine.entity.sprite.Sprite;

public class WallV2CollidedWithBubbleEventPayload implements EventPayload {

  public final int wallId;
  public Sprite bubbleSprite;

  public WallV2CollidedWithBubbleEventPayload(int wallId, Sprite bubbleSprite) {
    this.wallId = wallId;
    this.bubbleSprite = bubbleSprite;
  }
}
