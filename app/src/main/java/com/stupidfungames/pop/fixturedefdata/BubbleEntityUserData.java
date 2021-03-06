package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.BUBBLE;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import org.andengine.entity.sprite.Sprite;

public class BubbleEntityUserData extends BaseEntityUserData {

  public boolean isScoreLossBubble;
  public BubbleSpawnerEntity.BubbleSize size;
  public BubbleType bubbleType;
  public Sprite bubbleSprite;

  public boolean isTargeted;
  public boolean isMarkedForRecursivePopping;

  /**
   * @param isScoreLossBubble If true then the user will lose points when this bubble passes the
   * floor
   */
  public BubbleEntityUserData(
      boolean isScoreLossBubble,
      BubbleSpawnerEntity.BubbleSize size,
      BubbleType bubbleType,
      Sprite bubbleSprite) {
    super();
    this.isScoreLossBubble = isScoreLossBubble;
    this.size = size;
    this.bubbleType = bubbleType;
    this.bubbleSprite = bubbleSprite;
  }

  public void update(
      BubbleSpawnerEntity.BubbleSize size,
      BubbleType bubbleType) {
    this.size = size;
    this.bubbleType = bubbleType;
  }

  public static void markTargeted(@Nullable Sprite sprite, boolean isTargeted) {
    if (sprite != null) {
      @Nullable Object userdata = sprite.getUserData();
      if (userdata != null && userdata instanceof BubbleEntityUserData) {
        ((BubbleEntityUserData) userdata).isTargeted = isTargeted;
      }
    }
  }

  @Override
  public void reset() {
    super.reset();
    isScoreLossBubble = false;
    size = null;
    bubbleType = null;
    bubbleSprite = null;
    isMarkedForRecursivePopping = false;
    isTargeted = false;
  }

  @Override
  public int collisionType() {
    return BUBBLE;
  }
}
