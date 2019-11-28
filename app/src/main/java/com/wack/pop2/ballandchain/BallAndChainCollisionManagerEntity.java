package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.ChainLinkEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.WreckingBallEntityUserData;
import com.wack.pop2.utils.CoordinateConversionUtil;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

class BallAndChainCollisionManagerEntity extends BaseEntity implements GamePhysicsContactsEntity.GameContactListener {

    private GamePhysicsContactsEntity gamePhysicsContactsEntity;
    private BubblePopperEntity bubblePopperEntity;

    public BallAndChainCollisionManagerEntity(BubblePopperEntity bubblePopperEntity, GamePhysicsContactsEntity gamePhysicsContactsEntity, GameResources gameResources) {
        super(gameResources);
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
    }

    @Override
    public void onEndContact(final Fixture fixture1, final Fixture fixture2) {
        Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
        BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
        if (!bubbleEntityUserData.isPoppable()) {
            return;
        }
    }
}
