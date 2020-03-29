package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.ChainLinkEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.WreckingBallEntityUserData;

import org.jbox2d.dynamics.Fixture;

import static com.wack.pop2.eventbus.GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE;

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
