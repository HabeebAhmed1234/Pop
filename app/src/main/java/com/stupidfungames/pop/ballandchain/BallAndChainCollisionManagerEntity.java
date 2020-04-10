package com.stupidfungames.pop.ballandchain;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GamePhysicsContactsEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.ChainLinkEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.WreckingBallEntityUserData;

import org.jbox2d.dynamics.Fixture;

import static com.stupidfungames.pop.eventbus.GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE;

class BallAndChainCollisionManagerEntity extends BaseEntity implements GamePhysicsContactsEntity.GameContactListener {

    public BallAndChainCollisionManagerEntity(BinderEnity parent) {
        super(parent);
    }
    @Override
    public void onCreateScene() {
        super.onCreateScene();

        GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
        // Set up collision detection between the ball and chain and the bubbles on the stage
        gamePhysicsContactsEntity.addContactListener(ChainLinkEntityUserData.class, BubbleEntityUserData.class, this);
        gamePhysicsContactsEntity.addContactListener(WreckingBallEntityUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onDestroy() {
        GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
        gamePhysicsContactsEntity.removeContactListener(ChainLinkEntityUserData.class, BubbleEntityUserData.class, this);
        gamePhysicsContactsEntity.removeContactListener(WreckingBallEntityUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onBeginContact(Fixture fixture1, Fixture fixture2) {
        if (!shouldBallAndChainPop(get(BallAndChainStateMachine.class).getCurrentState())) {
            return;
        }
        Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
        BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
        if (!bubbleEntityUserData.isPoppable()) {
            return;
        }
        get(BubblePopperEntity.class).popBubble(
                bubbleEntityUserData.bubbleSprite,
                bubbleEntityUserData.size,
                bubbleEntityUserData.bubbleType);
        EventBus.get().sendEvent(BALL_AND_CHAIN_POPPED_BUBBLE);
    }

    @Override
    public void onEndContact(Fixture fixture1, Fixture fixture2) {}

    private boolean shouldBallAndChainPop(BallAndChainStateMachine.State state) {
        return state == BallAndChainStateMachine.State.IN_USE_CHARGED;
    }
}
