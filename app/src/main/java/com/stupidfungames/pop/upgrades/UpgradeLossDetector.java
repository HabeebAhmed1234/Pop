package com.stupidfungames.pop.upgrades;

import com.stupidfungames.pop.BaseLossDetectorEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.UpgradeUserData;
import com.stupidfungames.pop.physics.collision.CollisionIds;
import org.jbox2d.dynamics.Fixture;

public class UpgradeLossDetector extends BaseLossDetectorEntity {

  public UpgradeLossDetector(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected int getCollisionIdToDetectLossOf() {
    return CollisionIds.UPGRADE;
  }
  
  @Override
  protected void processLoss(Fixture upgradeFixture) {
    UpgradeUserData userData = (UpgradeUserData) upgradeFixture.getUserData();
    if (userData != null && userData.upgradeSprite != null && userData.upgradeSprite.isAttached()
        && userData.upgradeSprite.isVisible()) {
      removeFromScene(userData.upgradeSprite);
      get(UpgradeSpawner.class).onUpgradeLost();
    }
  }
}
