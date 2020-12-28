package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

public class BubblesInRadiusEntityMatcher extends BubblesEntityMatcher {

  private final float[] radiusCenterPx;
  private final float radiusPx;

  public BubblesInRadiusEntityMatcher(
      float[] radiusCenterPx,
      float radiusPx,
      boolean excludeTargetedBubbles,
      boolean onlyPoppableBubbles) {
    super(excludeTargetedBubbles, onlyPoppableBubbles);
    this.radiusCenterPx = radiusCenterPx;
    this.radiusPx = radiusPx;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    return super.matches(pEntity) && isInRadius(pEntity);
  }

  private boolean isInRadius(IEntity pEntity) {
    if (pEntity instanceof Sprite) {
      Sprite sprite = (Sprite) pEntity;
      float[] targetCenter = sprite.getCenter();
      return GeometryUtils.distanceBetween(
          radiusCenterPx[0], radiusCenterPx[1],
          targetCenter[0], targetCenter[1]) <= radiusPx;
    }
    return false;
  }
}
