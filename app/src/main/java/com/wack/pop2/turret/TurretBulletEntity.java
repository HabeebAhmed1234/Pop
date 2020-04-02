package com.wack.pop2.turret;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameFixtureDefs;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.collision.CollisionFilters;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.TurretBulletUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.CoordinateConversionUtil;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.OnDetachedListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import static com.wack.pop2.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;
import static com.wack.pop2.utils.GeometryUtils.getAngleOfCenters;

public class TurretBulletEntity extends BaseEntity implements EventBus.Subscriber {

    private static final float MOUSE_JOINT_DAMPING_RATIO = 0f;
    private static final float MOUSE_JOINT_FREQUENCY = 100;
    private static final float MOUSE_JOINT_MAX_FORCE_MULTIPLIER = 350.0f;

    private static final float TARGETING_UPDATE_INTERVAL = 1f / 30f;
    private static final float BULLET_MUZZLE_FORCE_MAGNITUDE = 1000;

    @Nullable private Sprite targetBubble;
    private MouseJoint targetingMouseJoint;

    private Sprite bulletSprite;
    private Body bulletBody;

    private final OnDetachedListener targetBubbleOnDetachedListener = new OnDetachedListener() {
        @Override
        public void onDetached(IEntity entity) {
            entity.removeOnDetachedListener(this);
            targetBubble = TurretUtils.getClosestPoppableBubble(scene, bulletSprite);
            if (targetBubble != null) {
                targetBubble.addOnDetachedListener(targetBubbleOnDetachedListener);
            } else {
                destroyBullet();
            }
        }
    };

    private final TimerHandler bulletTargetingUpdater = new TimerHandler(TARGETING_UPDATE_INTERVAL, true, new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
            if (targetBubble != null && targetingMouseJoint != null) {
                targetingMouseJoint.setTarget(
                        CoordinateConversionUtil.sceneToPhysicsWorld(
                                Vec2Pool.obtain(
                                        targetBubble.getX() + targetBubble.getWidthScaled() / 2 ,
                                        targetBubble.getY() + targetBubble.getHeightScaled() / 2)));
            }
        }
    });

    public TurretBulletEntity(Sprite targetBubble, BinderEnity parent) {
        super(parent);
        this.targetBubble = targetBubble;
        targetBubble.addOnDetachedListener(targetBubbleOnDetachedListener);
        initBullet();
        registerUpdateHandlers();
    }

    @Override
    public void onCreateScene() {
        registerUpdateHandlers();
    }

    @Override
    public void onDestroy() {
        unregisterUpdateHandlers();
    }

    private void initBullet() {
        Sprite turretCannonSprite = get(HostTurretCallback.class).getTurretCannonSprite();
        ITextureRegion turretBulletTexture = get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET);
        float[] turretCannonTipLocalPosition = new float[]{turretCannonSprite.getWidthScaled(), turretCannonSprite.getHeightScaled() / 2};
        turretCannonSprite.getLocalToSceneTransformation().transform(turretCannonTipLocalPosition);
        bulletSprite = new Sprite(
                turretCannonTipLocalPosition[0],
                turretCannonTipLocalPosition[1],
                turretBulletTexture,
                vertexBufferObjectManager);
        bulletSprite.setColor(AndengineColor.RED);
        TurretBulletUserData userData = new TurretBulletUserData();
        bulletSprite.setUserData(userData);

        final FixtureDef bulletFixtureDef = GameFixtureDefs.TURRET_BULLET_FIXTURE_DEF;
        bulletFixtureDef.setFilter(CollisionFilters.BULLET_FILTER);
        bulletFixtureDef.setUserData(userData);
        bulletBody = PhysicsFactory.createCircleBody(physicsWorld, bulletSprite, BodyType.DYNAMIC, bulletFixtureDef);
        bulletBody.setGravityScale(0);

        targetingMouseJoint = createBulletTargetingMouseJoint(bulletSprite, bulletBody);
        addToScene(bulletSprite, bulletBody);

        //TODO fix this
        // applyInitForceToBullet();
    }

    private void applyInitForceToBullet() {
        if (targetBubble != null) {
            Sprite turretBody = get(HostTurretCallback.class).getTurretBodySprite();
            float angle = getAngleOfCenters(turretBody, targetBubble);
            float xComponentForce = BULLET_MUZZLE_FORCE_MAGNITUDE * (float) Math.cos(angle);
            float yComponentForce = BULLET_MUZZLE_FORCE_MAGNITUDE * (float) Math.sin(angle);
            bulletBody.applyForceToCenter(Vec2Pool.obtain(xComponentForce, yComponentForce));
        }
    }

    private MouseJoint createBulletTargetingMouseJoint(final Sprite sprite, final Body body) {
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

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == TURRET_BULLET_POPPED_BUBBLE) {
            destroyBullet();
        }
    }

    private void registerUpdateHandlers() {
        if (!engine.containsUpdateHandler(bulletTargetingUpdater)) {
            engine.registerUpdateHandler(bulletTargetingUpdater);
        }
        if (!EventBus.get().containsSubscriber(TURRET_BULLET_POPPED_BUBBLE, this)) {
            EventBus.get().subscribe(TURRET_BULLET_POPPED_BUBBLE, this);
        }
    }

    private void unregisterUpdateHandlers() {
        engine.unregisterUpdateHandler(bulletTargetingUpdater);
        if (EventBus.get().containsSubscriber(TURRET_BULLET_POPPED_BUBBLE, this)) {
            EventBus.get().unSubscribe(TURRET_BULLET_POPPED_BUBBLE, this);
        }
    }

    /**
     * Blow up the bullet! Its goneeee.
     * TODO: figure out how to dispose of "this" object
     */
    private void destroyBullet() {
        unregisterUpdateHandlers();
        if (targetBubble != null) {
            targetBubble.removeOnDetachedListener(targetBubbleOnDetachedListener);
            targetBubble = null;
        }
        get(BulletExplosionsBaseEntity.class).explode(
                bulletSprite.getX() + bulletSprite.getWidthScaled() / 2,
                bulletSprite.getY() + bulletSprite.getHeightScaled() / 2);
        removeFromScene(bulletBody);

        physicsWorld.destroyBody(targetingMouseJoint.getBodyA());
        physicsWorld.destroyBody(targetingMouseJoint.getBodyB());
        physicsWorld.destroyJoint(targetingMouseJoint);
        bulletSprite = null;
        bulletBody = null;
    }
}
