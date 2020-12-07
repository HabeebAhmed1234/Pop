package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.FloorEntityUserData;
import org.jbox2d.dynamics.Fixture;

public abstract class BaseLossDetectorEntity extends BaseEntity {

  public BaseLossDetectorEntity(BinderEnity parent) {
    super(parent);
  }

  private final GamePhysicsContactsEntity.GameContactListener contactListener = new GamePhysicsContactsEntity.GameContactListener() {
    @Override
    public void onBeginContact(Fixture fixture1, Fixture fixture2) {
    }

    @Override
    public void onEndContact(Fixture fixture1, Fixture fixture2) {
      processLoss(FixtureDefDataUtil.getNonFloorFixture(fixture1, fixture2));
    }
  };

  @Override
  public void onCreateScene() {
    get(GamePhysicsContactsEntity.class)
        .addContactListener(getUserDataClassToDetectLossOf(), FloorEntityUserData.class,
            contactListener);
  }

  @Override
  public void onDestroy() {
    get(GamePhysicsContactsEntity.class)
        .removeContactListener(getUserDataClassToDetectLossOf(), FloorEntityUserData.class,
            contactListener);
  }

  protected abstract Class getUserDataClassToDetectLossOf();

  protected abstract void processLoss(Fixture lostFixture);
}
