package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.WRECKING_BALL;

public class WreckingBallEntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return WRECKING_BALL;
  }
}
