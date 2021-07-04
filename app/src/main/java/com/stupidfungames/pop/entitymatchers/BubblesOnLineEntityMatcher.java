package com.stupidfungames.pop.entitymatchers;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BUBBLE_BODY_SCALE_FACTOR;
import static com.stupidfungames.pop.utils.GeometryUtils.isCircleLineIntersecting;

import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

public class BubblesOnLineEntityMatcher extends BubblesEntityMatcher {

  private float x1;
  private float y1;
  private float x2;
  private float y2;

  public BubblesOnLineEntityMatcher(float x1, float y1, float x2, float y2) {
    super(false, true);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    return super.matches(pEntity) && isIntersectingWithLine(pEntity);
  }

  public void setLine(float x1, float y1, float x2, float y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  private boolean isIntersectingWithLine(IEntity pEntity) {
    if (pEntity instanceof Sprite) {
      Sprite sprite = (Sprite) pEntity;
      return isCircleLineIntersecting(
          x1, y1, x2, y2, sprite.getCenterX(), sprite.getCenterY(),
          sprite.getWidthScaled() * BUBBLE_BODY_SCALE_FACTOR / 2);
    }
    return false;
  }

}
