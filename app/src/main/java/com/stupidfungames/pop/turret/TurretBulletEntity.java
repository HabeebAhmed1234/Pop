package com.stupidfungames.pop.turret;

import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.CoordinateConversionUtil;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.OnDetachedListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class TurretBulletEntity extends BaseEntity implements EventBus.Subscriber {

  private static final float MOUSE_JOINT_DAMPING_RATIO = 0f;
  private static final float MOUSE_JOINT_FREQUENCY = 100;
  private static final float MOUSE_JOINT_MAX_FORCE_MULTIPLIER = 350.0f;

  private static final float TARGETING_UPDATE_INTERVAL = 1f / 30f;
  private static final float BULLET_MUZZLE_FORCE_MAGNITUDE = 1000;

  @Nullable
  private Sprite targetBubble;
  private MouseJoint targetingMouseJoint;

  private Sprite bulletSprite;
  private SettableFuture<Body> bulletBodyFuture = SettableFuture.create();

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

  private final TimerHandler bulletTargetingUpdater = new TimerHandler(TARGETING_UPDATE_INTERVAL,
      true, new ITimerCallback() {
    @Override
    public void onTimePassed(TimerHandler pTimerHandler) {
      if (targetBubble != null && targetingMouseJoint != null) {
        targetingMouseJoint.setTarget(
            CoordinateConversionUtil.sceneToPhysicsWorld(
                Vec2Pool.obtain(
                    targetBubble.getX() + targetBubble.getWidthScaled() / 2,
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
    ITextureRegion turretBulletTexture = get(GameTexturesManager.class)
        .getTextureRegion(TextureId.BULLET);
    float[] turretCannonTipLocalPosition = new float[]{turretCannonSprite.getWidthScaled(),
        turretCannonSprite.getHeightScaled() / 2};
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
    physicsWorld.postRunnable(new Runnable() {
      @Override
      public void run() {
        final Body bulletBody = PhysicsFactory
            .createCircleBody(physicsWorld, bulletSprite, BodyType.DYNAMIC, bulletFixtureDef);
        bulletBody.setGravityScale(0);

        targetingMouseJoint = createBulletTargetingMouseJoint(bulletSprite, bulletBody);
        addToScene(bulletSprite, bulletBody);
        bulletBodyFuture.set(bulletBody);
      }
    });
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
   * Blow up the bullet! Its goneeee. TODO: figure out how to dispose of "this" object
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
    Futures.addCallback(bulletBodyFuture, new FutureCallback<Body>() {
      @Override
      public void onSuccess(@NullableDecl Body result) {
        if (result != null) {
          removeFromScene(result);
        }
      }

      @Override
      public void onFailure(Throwable t) {
      }
    }, ContextCompat.getMainExecutor(get(Context.class)));

    physicsWorld.destroyBody(targetingMouseJoint.getBodyA());
    physicsWorld.destroyBody(targetingMouseJoint.getBodyB());
    physicsWorld.destroyJoint(targetingMouseJoint);
    bulletSprite = null;
    bulletBodyFuture = null;
  }
}
