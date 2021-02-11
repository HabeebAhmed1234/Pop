package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.FLOOR;

public class FloorEntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return FLOOR;
  }
}
