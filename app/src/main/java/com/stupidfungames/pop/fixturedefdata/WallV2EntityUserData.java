package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.WALL_V2;

public class WallV2EntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return WALL_V2;
  }
}
