package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

public class BubblesInRadiusEntityMatcher extends BubblesEntityMatcher {

  private final float radiusCenterXPx;
  private final float radiusCenterYPx;
  private final float radiusPx;

  public BubblesInRadiusEntityMatcher(
      float radiusCenterXPx,
      float radiusCenterYPx,
      float radiusPx,
      boolean excludeTargetedBubbles,
      boolean onlyPoppableBubbles) {
    super(excludeTargetedBubbles, onlyPoppableBubbles);
    this.radiusCenterXPx = radiusCenterXPx;
    this.radiusCenterYPx = radiusCenterYPx;
    this.radiusPx = radiusPx;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    return super.matches(pEntity) && isInRadius(pEntity);
  }

  private boolean isInRadius(IEntity pEntity) {
    if (pEntity instanceof Sprite) {
      Sprite sprite = (Sprite) pEntity;
      return GeometryUtils.distanceBetween(
          radiusCenterXPx, radiusCenterYPx,
          sprite.getCenterX(), sprite.getCenterY()) <= radiusPx;
    }
    return false;
  }
}
