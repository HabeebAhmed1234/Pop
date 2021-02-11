package com.stupidfungames.pop.fixturedefdata;

import static com.stupidfungames.pop.physics.collision.CollisionIds.UPGRADE;

import org.andengine.entity.sprite.Sprite;

public class UpgradeUserData extends BaseEntityUserData {

  public Sprite upgradeSprite;

  public UpgradeUserData(Sprite upgradeSprite) {
    this.upgradeSprite = upgradeSprite;
  }

  @Override
  public void reset() {
    super.reset();
    upgradeSprite = null;
  }

  @Override
  public int collisionType() {
    return UPGRADE;
  }
}
