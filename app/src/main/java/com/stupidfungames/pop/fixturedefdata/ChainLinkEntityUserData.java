package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.CHAIN_LINK;

public class ChainLinkEntityUserData extends BaseEntityUserData {

  @Override
  public int collisionType() {
    return CHAIN_LINK;
  }
}
