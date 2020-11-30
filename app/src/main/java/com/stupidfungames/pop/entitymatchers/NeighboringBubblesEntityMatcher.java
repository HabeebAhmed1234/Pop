package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.sprite.Sprite;

/**
 * Finds all the visible neighboring bubbles of the given bubble within the given radius.
 */
public class NeighboringBubblesEntityMatcher implements IEntityMatcher {

  private final float radius;
  private final Sprite bubble;

  public NeighboringBubblesEntityMatcher(final float radius, final Sprite bubble) {
    this.radius = radius;
    this.bubble = bubble;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    Object userData = pEntity.getUserData();
    if (userData instanceof BubbleEntityUserData) {
      Sprite sprite = ((Sprite) pEntity);
      float[] c1 = bubble.getCenter();
      float[] c2 = sprite.getCenter();
      if (sprite.isVisible() && GeometryUtils
          .distanceBetween(c1[0], c1[1], c2[0], c2[1], bubble.getWidthScaled() / 2,
              sprite.getWidthScaled() / 2) <= radius) {
        return true;
      }
    }
    return false;
  }
}
