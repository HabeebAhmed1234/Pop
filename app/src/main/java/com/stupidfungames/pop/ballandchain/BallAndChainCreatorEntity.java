package com.stupidfungames.pop.ballandchain;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.fixturedefdata.ChainLinkEntityUserData;
import com.stupidfungames.pop.fixturedefdata.WreckingBallEntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.CoordinateConversionUtil;
import com.stupidfungames.pop.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import java.util.HashSet;
import java.util.Set;

import static com.stupidfungames.pop.GameFixtureDefs.BASE_CHAIN_LINK_FIXTURE_DEF;
import static com.stupidfungames.pop.GameFixtureDefs.BASE_WRECKING_BALL_DEF;
import static com.stupidfungames.pop.ballandchain.BallAndChainHandleEntity.OFF_SCREEN_HANDLE_POSITION;

/**
 * Creates a ball and chain in the game
 */
class BallAndChainCreatorEntity extends BaseEntity {

    private static final int NUM_CHAIN_LINKS = 4;
    private static final float JOINT_ANCHOR_PERCENT_FROM_EDGE =  0.05f;
    private static final float MOUSE_JOINT_DAMPING_RATIO = 0f;
    private static final float MOUSE_JOINT_FREQUENCY = 100;
    private static final float MOUSE_JOINT_MAX_FORCE_MULTIPLIER = 8000.0f;

    private Pair<Sprite, Body> lastChainLink;

    public BallAndChainCreatorEntity(BinderEnity parent) {
        super(parent);
    }

    public BallAndChain createBallAndChain() {
        Set<Sprite> components = new HashSet<>();
        Pair<Sprite, Body> wreckingBall = createBall(
                OFF_SCREEN_HANDLE_POSITION.add(
                        Vec2Pool.obtain(
                                0 - ScreenUtils.getSreenSize().widthPx,
                                ScreenUtils.getSreenSize().heightPx / 3)));
        components.add(wreckingBall.first);

        Pair<Sprite, Body> previousChainLink = createChainLinkAndJoin(wreckingBall.first, wreckingBall.second);

        for (int i = 1 ; i < NUM_CHAIN_LINKS ; i++) {
            components.add(previousChainLink.first);
            previousChainLink = createChainLinkAndJoin(previousChainLink.first, previousChainLink.second);
        }

        lastChainLink = previousChainLink;
        components.add(lastChainLink.first);

        return new BallAndChain(createMouseJoint(lastChainLink.first, lastChainLink.second), components);
    }

    private Pair<Sprite, Body> createBall(final Vec2 position) {
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        ITextureRegion ballTexture = get(GameTexturesManager.class).getTextureRegion(TextureId.BALL);
        float x = 0;
        float y = screenSize.heightPx / 2;
        WreckingBallEntityUserData wreckingBallEntityUserData = new WreckingBallEntityUserData();
        final Sprite ballSprite = new Sprite(
                position.x,
                position.y,
                ballTexture,
                vertexBufferObjectManager);
        ballSprite.setUserData(wreckingBallEntityUserData);
        FixtureDef ballFixtureDef = BASE_WRECKING_BALL_DEF;
        ballFixtureDef.setFilter(CollisionFilters.BALL_AND_CHAIN_FILTER);
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
            @Nullable Body previousChainLinkBody) {

        ITextureRegion chainLinkTexture = get(GameTexturesManager.class).getTextureRegion(TextureId.CHAIN_LINK);
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        float previousChainLinkX = previousChainLinkSprite != null ? previousChainLinkSprite.getX() : screenSize.widthPx / 2 - chainLinkTexture.getWidth() / 2;
        float previousChainLinkY = previousChainLinkSprite != null ? previousChainLinkSprite.getY() : screenSize.heightPx / 2 - chainLinkTexture.getHeight() / 2;

        // The distance from the left or right edge of the chain link that we will place the anchor
        float jointPixelsFromEdge = JOINT_ANCHOR_PERCENT_FROM_EDGE * chainLinkTexture.getWidth();

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
        chainLinkFixtureDef.setFilter(CollisionFilters.BALL_AND_CHAIN_FILTER);
        chainLinkFixtureDef.setUserData(chainLinkEntityUserData);
        Body chainLinkBody = PhysicsFactory.createBoxBody(physicsWorld, chainLinkSprite, BodyType.DYNAMIC, chainLinkFixtureDef);

        addToScene(chainLinkSprite, chainLinkBody);

        // We don't need a joint if this is the first chain link
        boolean shouldCreateJoint = previousChainLinkBody != null;
        if (shouldCreateJoint) {
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.initialize(
                    previousChainLinkBody,
                    chainLinkBody,
                    CoordinateConversionUtil.sceneToPhysicsWorld(
                            new Vec2(newChainLinkAnchorX, newChainLinkAnchorY)));
            jointDef.collideConnected = false;
            physicsWorld.createJoint(jointDef);
        }
        return new Pair<>(chainLinkSprite, chainLinkBody);
    }

    private MouseJoint createMouseJoint(final Sprite sprite, final Body body) {
        final Vec2 physicsWorldPoint = CoordinateConversionUtil.sceneToPhysicsWorld(
                new Vec2(sprite.getX() + sprite.getWidth() / 2,
                        sprite.getY() + sprite.getHeight() / 2));

        final MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = physicsWorld.createBody(new BodyDef());
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = MOUSE_JOINT_DAMPING_RATIO;
        mouseJointDef.frequencyHz = MOUSE_JOINT_FREQUENCY;
        mouseJointDef.maxForce = (MOUSE_JOINT_MAX_FORCE_MULTIPLIER * body.getMass());
        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(physicsWorldPoint);

        return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
    }
}
