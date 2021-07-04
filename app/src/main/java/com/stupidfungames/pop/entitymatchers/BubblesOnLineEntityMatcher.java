package com.stupidfungames.pop.entitymatchers;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BUBBLE_BODY_SCALE_FACTOR;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

public class BubblesOnLineEntityMatcher extends BubblesEntityMatcher {

  private float pX;
  private float pY;

  public BubblesOnLineEntityMatcher(float pX, float pY, float p2X, float p2Y) {
    super(false, true);
    this.pX = pX;
    this.pY = pY;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    return super.matches(pEntity) && isInPoint(pEntity);
  }

  public void setPoint(float pX, float pY) {
    this.pX = pX;
    this.pY = pY;
  }

  private boolean isInPoint(IEntity pEntity) {
    if (pEntity instanceof Sprite) {
      Sprite sprite = (Sprite) pEntity;
      float bubbleRadius = sprite.getWidth() * BUBBLE_BODY_SCALE_FACTOR / 2;
      float centerX = sprite.getCenterX();
      float centerY = sprite.getCenterY();
      return Math.pow(pX - centerX, 2) + Math.pow(pY - centerY, 2) < Math.pow(bubbleRadius, 2);
    }
    return false;
  }

}
