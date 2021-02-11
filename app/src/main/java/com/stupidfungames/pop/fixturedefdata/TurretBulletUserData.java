package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.TURRET_BULLET;

public class TurretBulletUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return TURRET_BULLET;
  }
}
