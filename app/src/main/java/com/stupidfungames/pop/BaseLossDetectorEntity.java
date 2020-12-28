package com.stupidfungames.pop;

import androidx.annotation.Nullable;
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
      @Nullable Fixture lossFixture = FixtureDefDataUtil.getNonFloorFixture(fixture1, fixture2);
      if (lossFixture != null) {
        @Nullable Object userData = lossFixture.getUserData();
        Class lossUserDataClass = getUserDataClassToDetectLossOf();
        if (userData != null && lossUserDataClass.isInstance(userData)) {
          processLoss(lossFixture);
        }
      }
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

  /**
   * TODO can be called multiple times for the same fixture. FIX IT
   */
  protected abstract void processLoss(Fixture lostFixture);
}