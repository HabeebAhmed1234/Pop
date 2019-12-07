package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.ChainLinkEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.WreckingBallEntityUserData;
import com.wack.pop2.utils.CoordinateConversionUtil;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import static com.wack.pop2.eventbus.GameEvent.BALL_AND_CHAIN_POPPED_BUBBLE;

class BallAndChainCollisionManagerEntity extends BaseEntity implements GamePhysicsContactsEntity.GameContactListener {

    private BallAndChainStateMachine stateMachine;
    private GamePhysicsContactsEntity gamePhysicsContactsEntity;
    private BubblePopperEntity bubblePopperEntity;

    public BallAndChainCollisionManagerEntity(
            BallAndChainStateMachine stateMachine,
            BubblePopperEntity bubblePopperEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.bubblePopperEntity = bubblePopperEntity;
        this.gamePhysicsContactsEntity = gamePhysicsContactsEntity;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();

        // Set up collision detection between the ball and chain and the bubbles on the stage
        gamePhysicsContactsEntity.addContactListener(ChainLinkEntityUserData.class, BubbleEntityUserData.class, this);
        gamePhysicsContactsEntity.addContactListener(WreckingBallEntityUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onDestroy() {
        gamePhysicsContactsEntity.removeContactListener(ChainLinkEntityUserData.class, BubbleEntityUserData.class, this);
        gamePhysicsContactsEntity.removeContactListener(WreckingBallEntityUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onBeginContact(Fixture fixture1, Fixture fixture2) {
        if (!shouldBallAndChainPop(stateMachine.getCurrentState())) {
            return;
        }
        Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
        BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
        if (!bubbleEntityUserData.isPoppable()) {
            return;
        }
        Body bubbleBody  = bubbleFixture.getBody();
        bubblePopperEntity.popBubble(
                bubbleEntityUserData.bubbleSprite,
                bubbleEntityUserData.size,
                CoordinateConversionUtil.physicsWorldToScene(bubbleBody.getPosition()),
                bubbleEntityUserData.bubbleType);
        EventBus.get().sendEvent(BALL_AND_CHAIN_POPPED_BUBBLE);
    }

    @Override
    public void onEndContact(Fixture fixture1, Fixture fixture2) {}

    private boolean shouldBallAndChainPop(BallAndChainStateMachine.State state) {
        return state == BallAndChainStateMachine.State.IN_USE_CHARGED;
    }
}
