package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.eventbus.GameEvent.WALL_V2_POPPED_BUBBLE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.WallV2PoppedBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.physics.collision.CollisionIds;
import com.stupidfungames.pop.physics.collision.GamePhysicsContactsEntity;
import org.jbox2d.dynamics.Fixture;

public class WallsV2CollisionManager extends BaseEntity implements
    GamePhysicsContactsEntity.GameContactListener {

  public WallsV2CollisionManager(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
    gamePhysicsContactsEntity
        .addContactListener(CollisionIds.WALL_V2, CollisionIds.BUBBLE, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
    gamePhysicsContactsEntity
        .removeContactListener(CollisionIds.WALL_V2, CollisionIds.BUBBLE, this);
  }

  @Override
  public void onBeginContact(Fixture fixture1, Fixture fixture2) {
    if (shouldWallPop()) {
      Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
      BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture
          .getUserData();
      if (bubbleEntityUserData.bubbleSprite.isVisible()) {
        get(BubblePopperEntity.class).popBubble(bubbleEntityUserData.bubbleSprite);

        Fixture wallFixture = FixtureDefDataUtil.getWallV2Fixture(fixture1, fixture2);
        EventBus.get().sendEvent(
            WALL_V2_POPPED_BUBBLE,
            new WallV2PoppedBubbleEventPayload(
                ((BaseEntityUserData) wallFixture.m_userData).getId()));
      }
    }
  }

  @Override
  public void onEndContact(Fixture fixture1, Fixture fixture2) {
  }

  private boolean shouldWallPop() {
    return true;
  }
}
