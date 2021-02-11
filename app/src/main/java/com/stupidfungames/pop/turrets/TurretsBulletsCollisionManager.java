package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.TurretBulletPoppedBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import com.stupidfungames.pop.physics.collision.CollisionIds;
import com.stupidfungames.pop.physics.collision.GamePhysicsContactsEntity;
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
        .removeContactListener(CollisionIds.TURRET_BULLET, CollisionIds.BUBBLE, this);
  }

  @Override
  public void onBeginContact(Fixture fixture1, Fixture fixture2) {
    Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
    BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
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
        .containsContactListener(CollisionIds.TURRET_BULLET, CollisionIds.BUBBLE, this)) {
      gamePhysicsContactsEntity
          .addContactListener(CollisionIds.TURRET_BULLET, CollisionIds.BUBBLE, this);
    }
  }
}
