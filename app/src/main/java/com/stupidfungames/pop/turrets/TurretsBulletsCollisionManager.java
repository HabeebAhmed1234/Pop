package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GamePhysicsContactsEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.TurretBulletPoppedBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import org.jbox2d.dynamics.Fixture;

public class TurretsBulletsCollisionManager extends BaseEntity implements
    GamePhysicsContactsEntity.GameContactListener {

  public TurretsBulletsCollisionManager(BinderEnity parent) {
    super(parent);
    addContactListener();
  }

  @Override
  protected void createBindings(Binder binder) {
  }

  @Override
  public void onCreateScene() {
    addContactListener();
  }

  @Override
  public void onDestroy() {
    get(GamePhysicsContactsEntity.class)
        .removeContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
  }

  @Override
  public void onBeginContact(Fixture fixture1, Fixture fixture2) {
    Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
    BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
    if (!bubbleEntityUserData.isPoppable()) {
      return;
    }
    if (get(BubblePopperEntity.class).popBubble(bubbleEntityUserData.bubbleSprite)) {
      Fixture bulletFixture = FixtureDefDataUtil.getBulletFixture(fixture1, fixture2);
      TurretBulletUserData bulletUserData = (TurretBulletUserData) bulletFixture.getUserData();
      EventBus.get().sendEvent(TURRET_BULLET_POPPED_BUBBLE,
          new TurretBulletPoppedBubbleEventPayload(bulletUserData.getId()));
    }
  }

  @Override
  public void onEndContact(Fixture fixture1, Fixture fixture2) {
  }

  private void addContactListener() {
    GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
    if (!gamePhysicsContactsEntity
        .containsContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this)) {
      gamePhysicsContactsEntity
          .addContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
    }
  }
}
