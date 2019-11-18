package com.wack.pop2;

import android.util.Pair;

import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.ChainLinkEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.WreckingBallEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.physics.util.constants.PhysicsConstants;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import androidx.annotation.Nullable;

import static com.wack.pop2.GameFixtureDefs.BASE_CHAIN_LINK_FIXTURE_DEF;
import static com.wack.pop2.GameFixtureDefs.BASE_WRECKING_BALL_DEF;

/**
 * Manages the ball and chain tool that can be used to pop bubbles by the user swinging around a
 * spike ball on a chain.
 */
public class BallAndChainManagerEntity extends BaseEntity implements IOnSceneTouchListener, GamePhysicsContactsEntity.GameContactListener {

    private static final int NUM_CHAIN_LINKS = 4;

    private GameTexturesManager texturesManager;
    private GamePhysicsContactsEntity contactsEntity;
    private BubblePopperEntity bubblePopperEntity;
    private ScreenUtils.ScreenSize screenSize;

    private Pair<Sprite, Body> lastChainLink;
    private MouseJoint mouseJoint;

    public BallAndChainManagerEntity(
            GameTexturesManager texturesManager,
            GamePhysicsContactsEntity contactsEntity,
            BubblePopperEntity bubblePopperEntity,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.contactsEntity = contactsEntity;
        this.bubblePopperEntity = bubblePopperEntity;
        this.screenSize = ScreenUtils.getSreenSize();
    }

    @Override
    public void onCreateScene() {
        createBallAndChain();
        scene.setOnSceneTouchListener(this);

        // Set up collision detection between the ball and chain and the bubbles on the stage
        contactsEntity.addContactListener(ChainLinkEntityUserData.class, BubbleEntityUserData.class, this);
        contactsEntity.addContactListener(WreckingBallEntityUserData.class, BubbleEntityUserData.class, this);
    }

    private void createBallAndChain() {
        final float anchorPercentFromEdge = 0.05f;
        Pair<Sprite, Body> wreckingBall = createBall();
        Pair<Sprite, Body> previousChainLink = createChainLinkAndJoin(wreckingBall.first, wreckingBall.second, anchorPercentFromEdge, false);
        for (int i = 1 ; i < NUM_CHAIN_LINKS ; i++) {
            previousChainLink = createChainLinkAndJoin(previousChainLink.first, previousChainLink.second, anchorPercentFromEdge, i == NUM_CHAIN_LINKS - 1);
        }
        lastChainLink = previousChainLink;

        mouseJoint = createMouseJoint(lastChainLink.first, lastChainLink.second);
    }

    private Pair<Sprite, Body> createBall() {
        ITextureRegion ballTexture = texturesManager.getTextureRegion(TextureId.GREEN_BUBBLE);
        float x = 0;
        float y = screenSize.height / 2;
        WreckingBallEntityUserData wreckingBallEntityUserData = new WreckingBallEntityUserData();
        final Sprite ballSprite = new Sprite(
                x,
                y,
                ballTexture,
                vertexBufferObjectManager);
        ballSprite.setUserData(wreckingBallEntityUserData);
        FixtureDef ballFixtureDef = BASE_WRECKING_BALL_DEF;
        ballFixtureDef.setUserData(wreckingBallEntityUserData);
        Body ballBody = PhysicsFactory.createCircleBody(
                physicsWorld,
                ballSprite,
                BodyType.DYNAMIC,
                ballFixtureDef);

        addToScene(ballSprite, ballBody);
        return new Pair<>(ballSprite, ballBody);
    }

    /**
     * Given a chain link (if no chain link is given then just creates a chain link in the center of the screen)
     * creates a new chain link and attaches it to the right ride of the given chain link.
     * @return
     */
    private Pair<Sprite, Body> createChainLinkAndJoin(
            @Nullable Sprite previousChainLinkSprite,
            @Nullable Body previousChainLinkBody,
            float jointPercentFromEdge,
            boolean isLastLink) {

        ITextureRegion chainLinkTexture = texturesManager.getTextureRegion(TextureId.CHAIN_LINK);
        float previousChainLinkX = previousChainLinkSprite != null ? previousChainLinkSprite.getX() : screenSize.width / 2 - chainLinkTexture.getWidth() / 2;
        float previousChainLinkY = previousChainLinkSprite != null ? previousChainLinkSprite.getY() : screenSize.height / 2 - chainLinkTexture.getHeight() / 2;

        // The distance from the left or right edge of the chain link that we will place the anchor
        float jointPixelsFromEdge = jointPercentFromEdge * chainLinkTexture.getWidth();

        // New chain link anchor position (on the right side of the given chain link)
        float newChainLinkAnchorX = previousChainLinkX + chainLinkTexture.getWidth() - jointPixelsFromEdge;
        float newChainLinkAnchorY = previousChainLinkY + chainLinkTexture.getHeight() / 2;

        float newChainLinkX = newChainLinkAnchorX - jointPixelsFromEdge;
        float newChainLinkY = previousChainLinkY;
        ChainLinkEntityUserData chainLinkEntityUserData = new ChainLinkEntityUserData();
        final Sprite chainLinkSprite = new Sprite(
                newChainLinkX,
                newChainLinkY,
                chainLinkTexture,
                vertexBufferObjectManager);
        chainLinkSprite.setUserData(chainLinkEntityUserData);
        FixtureDef chainLinkFixtureDef = BASE_CHAIN_LINK_FIXTURE_DEF;
        chainLinkFixtureDef.setUserData(chainLinkEntityUserData);
        Body chainLinkBody = PhysicsFactory.createBoxBody(physicsWorld, chainLinkSprite, isLastLink ? BodyType.DYNAMIC : BodyType.DYNAMIC, chainLinkFixtureDef);

        addToScene(chainLinkSprite, chainLinkBody);

        // We don't need a joint if this is the first chain link
        boolean shouldCreateJoint = previousChainLinkBody != null;
        if (shouldCreateJoint) {
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.initialize(previousChainLinkBody, chainLinkBody, new Vec2(newChainLinkAnchorX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, newChainLinkAnchorY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
            jointDef.collideConnected = false;
            //jointDef2.lowerAngle = 0;
            //jointDef2.upperAngle = 2 * (float) Math.PI;
            physicsWorld.createJoint(jointDef);
        }
        return new Pair<>(chainLinkSprite, chainLinkBody);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent touchEvent) {
        switch(touchEvent.getAction()) {
            case TouchEvent.ACTION_UP:
                return false;
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                setTarget(touchEvent);
                return true;
        }
        return false;
    }

    private void setTarget(TouchEvent touchEvent) {
        if (mouseJoint == null) return;
        final Vec2 vec = Vec2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        mouseJoint.setTarget(vec);
    }


    public MouseJoint createMouseJoint(final Sprite sprite, final Body body) {
        final MouseJointDef mouseJointDef = new MouseJointDef();

        final Vec2 localPoint = new Vec2(
                (sprite.getX() + sprite.getWidth() / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                (sprite.getY() + sprite.getHeight() / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);

        mouseJointDef.bodyA = physicsWorld.createBody(new BodyDef());
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = 0f;
        mouseJointDef.frequencyHz = 100;
        mouseJointDef.maxForce = (8000.0f * body.getMass());
        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(localPoint);

        return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
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
