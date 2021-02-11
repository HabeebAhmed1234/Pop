package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.LEVEL_WALL;

public class LevelWallEntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return LEVEL_WALL;
  }
}
