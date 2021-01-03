package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.utils.GeometryUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

/**
 * Finds all the visible neighboring bubbles of the given bubble within the given radius.
 */
public class NeighboringBubblesMarkedForRecursivePoppingEntityMatcher extends BubblesEntityMatcher {

  private final float radius;
  private final Sprite bubble;
  private final BubbleType bubbleType;

  public NeighboringBubblesMarkedForRecursivePoppingEntityMatcher(final float radius,
      final Sprite bubble, BubbleType bubbleType) {
    super(false, true);
    this.radius = radius;
    this.bubble = bubble;
    this.bubbleType = bubbleType;
  }

  @Override
  public boolean matches(IEntity pEntity) {
    if (!super.matches(pEntity)) {
      return false;
    }
    Object userData = pEntity.getUserData();
    if (userData instanceof BubbleEntityUserData) {
      BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) userData;
      if (!bubbleEntityUserData.isMarkedForRecursivePopping
          || bubbleEntityUserData.bubbleType != bubbleType) {
        return false;
      }
      Sprite sprite = ((Sprite) pEntity);
      if (GeometryUtils
          .distanceBetween(bubble.getCenterX(), bubble.getCenterY(), sprite.getCenterX(),
              sprite.getCenterY(), bubble.getWidthScaled() / 2,
              sprite.getWidthScaled() / 2) <= radius) {
        return true;
      }
    }
    return false;
  }
}
