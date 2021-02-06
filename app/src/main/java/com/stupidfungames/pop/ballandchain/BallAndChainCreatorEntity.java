package com.stupidfungames.pop.ballandchain;

import static com.stupidfungames.pop.GameFixtureDefs.BASE_CHAIN_LINK_FIXTURE_DEF;
import static com.stupidfungames.pop.GameFixtureDefs.BASE_WRECKING_BALL_DEF;
import static com.stupidfungames.pop.ballandchain.BallAndChainHandleEntity.OFF_SCREEN_HANDLE_POSITION;

import android.content.Context;
import android.util.Pair;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.fixturedefdata.ChainLinkEntityUserData;
import com.stupidfungames.pop.fixturedefdata.WreckingBallEntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.CoordinateConversionUtil;
import java.util.HashSet;
import java.util.Set;
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

/**
 * Creates a ball and chain in the game
 */
class BallAndChainCreatorEntity extends BaseEntity {

  private static final int NUM_CHAIN_LINKS = 2;
  private static final float JOINT_ANCHOR_PERCENT_FROM_EDGE = 0.05f;
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
        CoordinateConversionUtil.physicsWorldToScene(OFF_SCREEN_HANDLE_POSITION));
    components.add(wreckingBall.first);

    Pair<Sprite, Body> previousChainLink = createChainLinkAndJoin(wreckingBall.first,
        wreckingBall.second);

    for (int i = 1; i < NUM_CHAIN_LINKS; i++) {
      components.add(previousChainLink.first);
      previousChainLink = createChainLinkAndJoin(previousChainLink.first, previousChainLink.second);
    }

    lastChainLink = previousChainLink;
    components.add(lastChainLink.first);

    return new BallAndChain(createMouseJoint(lastChainLink.first, lastChainLink.second),
        components);
  }

  private Pair<Sprite, Body> createBall(final Vec2 position) {
    ITextureRegion ballTexture = get(GameTexturesManager.class).getTextureRegion(TextureId.BALL);
    WreckingBallEntityUserData wreckingBallEntityUserData = new WreckingBallEntityUserData();
    final Sprite ballSprite = new Sprite(
        0, //position.x,
        0, //position.y,
        ballTexture,
        vertexBufferObjectManager);
    ballSprite.setScale(BubbleSize.MEDIUM.sizePx / ballSprite.getWidth());
    ballSprite.setScaledPosition(position.x, position.y);
    ballSprite.setUserData(wreckingBallEntityUserData);
    FixtureDef ballFixtureDef = BASE_WRECKING_BALL_DEF;
    ballFixtureDef.setFilter(CollisionFilters.BALL_AND_CHAIN_FILTER);
    ballFixtureDef.setUserData(wreckingBallEntityUserData);
    Body ballBody = PhysicsFactory.createCircleBody(
        physicsWorld,
        ballSprite,
        BubbleSpawnerEntity.BUBBLE_BODY_SCALE_FACTOR,
        BodyType.DYNAMIC,
        ballFixtureDef);

    addToScene(ballSprite, ballBody);
    return new Pair<>(ballSprite, ballBody);
  }

  /**
   * Given a chain link (if no chain link is given then just creates a chain link in the center of
   * the screen) creates a new chain link and attaches it to the right ride of the given chain
   * link.
   */
  private Pair<Sprite, Body> createChainLinkAndJoin(
      Sprite previousChainLinkSprite,
      Body previousChainLinkBody) {

    ITextureRegion chainLinkTexture = get(GameTexturesManager.class)
        .getTextureRegion(TextureId.CHAIN_LINK);
    float chainLinkScaledWidth = BubbleSize.SMALL.sizePx;
    float chainLinkScaledHeight =
        chainLinkScaledWidth * (chainLinkTexture.getHeight() / chainLinkTexture.getWidth());
    float previousChainLinkX = previousChainLinkSprite.getScaledX();
    float previousChainLinkY = previousChainLinkSprite.getScaledY();

    // The distance from the left or right edge of the chain link that we will place the anchor
    float jointPixelsFromEdge = JOINT_ANCHOR_PERCENT_FROM_EDGE * chainLinkScaledWidth;

    // New chain link anchor position (on the right side of the given chain link)
    float newChainLinkAnchorX =
        previousChainLinkX + chainLinkScaledWidth - jointPixelsFromEdge;
    float newChainLinkAnchorY = previousChainLinkY + chainLinkScaledHeight / 2;

    float newChainLinkX = newChainLinkAnchorX - jointPixelsFromEdge;
    float newChainLinkY = previousChainLinkY;
    ChainLinkEntityUserData chainLinkEntityUserData = new ChainLinkEntityUserData();
    final Sprite chainLinkSprite = new Sprite(
        0,
        0,
        chainLinkTexture,
        vertexBufferObjectManager);
    chainLinkSprite
        .setScale(chainLinkScaledWidth / chainLinkSprite.getWidth());
    chainLinkSprite.setScaledPosition(newChainLinkX, newChainLinkY);
    chainLinkSprite.setUserData(chainLinkEntityUserData);
    FixtureDef chainLinkFixtureDef = BASE_CHAIN_LINK_FIXTURE_DEF;
    chainLinkFixtureDef.setFilter(CollisionFilters.BALL_AND_CHAIN_FILTER);
    chainLinkFixtureDef.setUserData(chainLinkEntityUserData);

    Body chainLinkBody = PhysicsFactory
        .createBoxBody(physicsWorld, chainLinkSprite, BodyType.DYNAMIC, chainLinkFixtureDef);

    addToScene(chainLinkSprite, chainLinkBody);

    RevoluteJointDef jointDef = new RevoluteJointDef();

    Vec2 newChainLinkAnchorPos = Vec2Pool.obtain(newChainLinkAnchorX, newChainLinkAnchorY);
    CoordinateConversionUtil.sceneToPhysicsWorld(newChainLinkAnchorPos);
    jointDef.initialize(
        previousChainLinkBody,
        chainLinkBody,
        newChainLinkAnchorPos);
    Vec2Pool.recycle(newChainLinkAnchorPos);
    jointDef.collideConnected = false;
    physicsWorld.createJoint(jointDef);
    return new Pair<>(chainLinkSprite, chainLinkBody);
  }

  private MouseJoint createMouseJoint(final Sprite sprite, final Body body) {
    Vec2 physicsWorldPoint = Vec2Pool.obtain(sprite.getScaledX() + sprite.getWidthScaled(),
        sprite.getScaledY() + sprite.getHeightScaled() / 2);
    CoordinateConversionUtil.sceneToPhysicsWorld(physicsWorldPoint);

    final MouseJointDef mouseJointDef = new MouseJointDef();
    mouseJointDef.bodyA = physicsWorld.createBody(new BodyDef());
    mouseJointDef.bodyB = body;
    mouseJointDef.dampingRatio = MOUSE_JOINT_DAMPING_RATIO;
    mouseJointDef.frequencyHz = MOUSE_JOINT_FREQUENCY;
    mouseJointDef.maxForce = (MOUSE_JOINT_MAX_FORCE_MULTIPLIER * body.getMass());
    mouseJointDef.collideConnected = true;

    mouseJointDef.target.set(physicsWorldPoint);
    Vec2Pool.recycle(physicsWorldPoint);

    return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
  }
}
