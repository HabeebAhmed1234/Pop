package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.BOMB_BUBBLE;

public class BombBubbleEntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return BOMB_BUBBLE;
  }
}
