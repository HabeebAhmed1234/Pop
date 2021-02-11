package com.stupidfungames.pop.ballandchain;

import static com.stupidfungames.pop.eventbus.GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.physics.collision.CollisionIds;
import com.stupidfungames.pop.physics.collision.GamePhysicsContactsEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.ChainLinkEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.WreckingBallEntityUserData;
import org.jbox2d.dynamics.Fixture;

class BallAndChainCollisionManagerEntity extends BaseEntity implements
    GamePhysicsContactsEntity.GameContactListener {

  public BallAndChainCollisionManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();

    GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
    // Set up collision detection between the ball and chain and the bubbles on the stage
    gamePhysicsContactsEntity
        .addContactListener(CollisionIds.CHAIN_LINK, CollisionIds.BUBBLE, this);
    gamePhysicsContactsEntity
        .addContactListener(CollisionIds.WRECKING_BALL, CollisionIds.BUBBLE, this);
  }

  @Override
  public void onDestroy() {
    GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
    gamePhysicsContactsEntity
        .removeContactListener(CollisionIds.CHAIN_LINK, CollisionIds.BUBBLE, this);
    gamePhysicsContactsEntity
        .removeContactListener(CollisionIds.WRECKING_BALL, CollisionIds.BUBBLE, this);
  }


  @Override
  public void onBeginContact(Fixture fixture1, Fixture fixture2) {
    /** NOOP */
  }

  @Override
  public void onEndContact(Fixture fixture1, Fixture fixture2) {
    if (!shouldBallAndChainPop(get(BallAndChainStateMachine.class).getCurrentState())) {
      return;
    }
    Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
    BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
    if (bubbleEntityUserData.bubbleSprite.isVisible()) {
      get(BubblePopperEntity.class).popBubble(bubbleEntityUserData.bubbleSprite);
      EventBus.get().sendEvent(BALL_AND_CHAIN_POPPED_BUBBLE);
    }
  }

  private boolean shouldBallAndChainPop(BallAndChainStateMachine.State state) {
    return state == BallAndChainStateMachine.State.IN_USE_CHARGED;
  }
}
