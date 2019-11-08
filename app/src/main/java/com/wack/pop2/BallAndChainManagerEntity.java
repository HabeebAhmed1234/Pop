package com.wack.pop2;

import android.util.Pair;

import com.wack.pop2.physics.PhysicsConnector;
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
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import androidx.annotation.Nullable;

import static com.wack.pop2.GameFixtureDefs.BASE_CHAIN_LINK_FIXTURE_DEF;

/**
 * Manages the ball and chain tool that can be used to pop bubbles by the user swinging around a
 * spike ball on a chain.
 */
public class BallAndChainManagerEntity extends BaseEntity implements IOnSceneTouchListener {

    private GameTexturesManager texturesManager;
    private ScreenUtils.ScreenSize screenSize;

    private MouseJoint mouseJoint;
    private Body pointerBody;
    private Pair<Sprite, Body> lastChainLink;

    public BallAndChainManagerEntity(
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
        this.screenSize = ScreenUtils.getSreenSize();
    }

    @Override
    public void onCreateScene() {
        createBallAndChain();

        pointerBody = physicsWorld.createBody(new BodyDef());
        scene.setOnSceneTouchListener(this);
    }

    private void createBallAndChain() {
        final float anchorPercentFromEdge = 0.25f;
        final int numChainLinks = 5;
        Pair<Sprite, Body> previousChainLink = createChainLinkAndJoin(null, null, anchorPercentFromEdge);
        for (int i = 1 ; i < numChainLinks ; i++) {
            previousChainLink = createChainLinkAndJoin(previousChainLink.first, previousChainLink.second, anchorPercentFromEdge);
        }
        lastChainLink = previousChainLink;
    }

    /**
     * Given a chain link (if no chain link is given then just creates a chain link in the center of the screen)
     * creates a new chain link and attaches it to the right ride of the given chain link.
     * @return
     */
    private Pair<Sprite, Body> createChainLinkAndJoin(
            @Nullable Sprite previousChainLinkSprite,
            @Nullable Body previousChainLinkBody,
            float jointPercentFromEdge) {

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
        final Sprite chainLinkSprite = new Sprite(
                newChainLinkX,
                newChainLinkY,
                chainLinkTexture,
                vertexBufferObjectManager);

        FixtureDef chainLinkFixtureDef3 = BASE_CHAIN_LINK_FIXTURE_DEF;

        Body chainLinkBody = PhysicsFactory.createBoxBody(physicsWorld, chainLinkSprite, BodyType.DYNAMIC, chainLinkFixtureDef3);
        chainLinkBody.setGravityScale(0.1f);
        PhysicsConnector chainLinkPhysicsConnector3 = new PhysicsConnector(chainLinkSprite, chainLinkBody, true, true);

        physicsWorld.registerPhysicsConnector(chainLinkPhysicsConnector3);
        addToScene(chainLinkSprite);

        // We don't need a joint if this is the first chain link
        boolean shouldCreateJoint = previousChainLinkBody != null;
        if (shouldCreateJoint) {
            RevoluteJointDef jointDef2 = new RevoluteJointDef();
            jointDef2.initialize(previousChainLinkBody, chainLinkBody, new Vec2(newChainLinkAnchorX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, newChainLinkAnchorY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
            jointDef2.collideConnected = false;
            //jointDef2.lowerAngle = 0;
            //jointDef2.upperAngle = 2 * (float) Math.PI;
            physicsWorld.createJoint(jointDef2);
        }
        return new Pair<>(chainLinkSprite, chainLinkBody);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        switch(pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                mouseJoint = createMouseJoint(lastChainLink.first, lastChainLink.second);
                break;
            case TouchEvent.ACTION_UP:
                return true;
            case TouchEvent.ACTION_MOVE:
                if(mouseJoint != null) {
                    final Vec2 vec = Vec2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                    mouseJoint.setTarget(vec);
                    Vec2Pool.recycle(vec);
                }
                return true;
        }
        return false;
    }

    public MouseJoint createMouseJoint(final Sprite lastChainLink, final Body lastChainLinkBody) {
        final MouseJointDef mouseJointDef = new MouseJointDef();

        final Vec2 centerChain = Vec2Pool.obtain(
                (lastChainLink.getX() + lastChainLink.getWidth() / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                (lastChainLink.getY() + lastChainLink.getHeight() / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);

        pointerBody.setTransform(centerChain, 0);

        mouseJointDef.bodyA = pointerBody;
        mouseJointDef.bodyB = lastChainLinkBody;
        mouseJointDef.dampingRatio = 0.1f;
        mouseJointDef.frequencyHz = 60;
        mouseJointDef.maxForce = 10000.0f;
        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(centerChain);

        return (MouseJoint) physicsWorld.createJoint(mouseJointDef);
    }
}
