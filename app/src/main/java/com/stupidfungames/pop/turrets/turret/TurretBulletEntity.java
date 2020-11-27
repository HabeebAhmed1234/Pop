package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_POPPED;
import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.eventbus.BubblePoppedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.TurretBulletPoppedBubbleEventPayload;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.pool.SpriteInitializerParams;
import com.stupidfungames.pop.turrets.BulletExplosionsEntity;
import com.stupidfungames.pop.turrets.TurretUtils;
import com.stupidfungames.pop.utils.CoordinateConversionUtil;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
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
  private static final int MAX_RETARGET_COUNT = 2;

  private static final float TARGETING_UPDATE_INTERVAL = 1f / 10f;

  private int id = -1;
  private int retargetCount = 0;
  private Sprite targetBubble;
  private MouseJoint targetingMouseJoint;
  private Sprite bulletSprite;
  private Body bulletBody;

  private boolean isDestroyed = false;

  private final TimerHandler bulletTargetingUpdater = new TimerHandler(TARGETING_UPDATE_INTERVAL,
      true, new ITimerCallback() {
    @Override
    public void onTimePassed(TimerHandler pTimerHandler) {
      if (targetBubble != null && targetBubble.isVisible()) {
        targetingMouseJoint.setTarget(
            CoordinateConversionUtil.sceneToPhysicsWorld(
                Vec2Pool.obtain(
                    targetBubble.getX() + targetBubble.getWidthScaled() / 2,
                    targetBubble.getY() + targetBubble.getHeightScaled() / 2)));
      } else {
        findNextTarget();
      }
    }
  });

  public TurretBulletEntity(Sprite targetBubble, BinderEnity parent) {
    super(parent);
    this.targetBubble = targetBubble;
    registerUpdateHandlers();
    initBullet();
  }

  @Override
  public void onCreateScene() {
    registerUpdateHandlers();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterUpdateHandlers();
    destroyBulletReferences();
  }

  private void initBullet() {
    Sprite turretCannonSprite = get(HostTurretCallback.class).getTurretCannonSprite();
    float[] turretCannonTipLocalPosition = new float[]{turretCannonSprite.getWidthScaled(),
        turretCannonSprite.getHeightScaled() / 2};
    turretCannonSprite.getLocalToSceneTransformation().transform(turretCannonTipLocalPosition);
    bulletSprite = (Sprite) get(TurretBulletSpritePool.class).get(
        new SpriteInitializerParams(turretCannonTipLocalPosition[0],
            turretCannonTipLocalPosition[1]));

    id = ((TurretBulletUserData) bulletSprite.getUserData()).getId();

    final FixtureDef bulletFixtureDef = GameFixtureDefs.TURRET_BULLET_FIXTURE_DEF;
    bulletFixtureDef.setFilter(CollisionFilters.BULLET_FILTER);
    bulletFixtureDef.setUserData(bulletSprite.getUserData());
    bulletBody = PhysicsFactory
        .createCircleBody(physicsWorld, bulletSprite, BodyType.DYNAMIC, bulletFixtureDef);
    bulletBody.setGravityScale(0);

    targetingMouseJoint = createBulletTargetingMouseJoint(bulletSprite, bulletBody);
    addToScene(bulletSprite, bulletBody);
  }

  private void findNextTarget() {
    if (retargetCount > MAX_RETARGET_COUNT) {
      destroyBullet();
    }
    retargetCount++;
    Sprite bubble = TurretUtils
        .getClosestPoppableBubble(scene, get(HostTurretCallback.class).getTurretBodySprite());
    if (bubble == null) {
      destroyBullet();
      return;
    }
    targetBubble = bubble;
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
    switch (event) {
      case BUBBLE_SPAWNED:
        if (targetBubble != null
            && ((BubblePoppedEventPayload) payload).bubbleId == ((BubbleEntityUserData) targetBubble
            .getUserData()).getId()) {
          targetBubble = null;
        }
        break;
      case TURRET_BULLET_POPPED_BUBBLE:
        if (((TurretBulletPoppedBubbleEventPayload) payload).bulletId == id) {
          destroyBullet();
        }
        break;
    }
  }

  private void registerUpdateHandlers() {
    if (!engine.containsUpdateHandler(bulletTargetingUpdater)) {
      engine.registerUpdateHandler(bulletTargetingUpdater);
    }
    if (!EventBus.get().containsSubscriber(BUBBLE_POPPED, this)) {
      EventBus.get().subscribe(BUBBLE_POPPED, this);
    }
    if (!EventBus.get().containsSubscriber(TURRET_BULLET_POPPED_BUBBLE, this)) {
      EventBus.get().subscribe(TURRET_BULLET_POPPED_BUBBLE, this);
    }
  }

  private void unregisterUpdateHandlers() {
    if (engine.containsUpdateHandler(bulletTargetingUpdater)) {
      engine.unregisterUpdateHandler(bulletTargetingUpdater);
    }
    if (EventBus.get().containsSubscriber(BUBBLE_POPPED, this)) {
      EventBus.get().unSubscribe(BUBBLE_POPPED, this);
    }
    if (EventBus.get().containsSubscriber(TURRET_BULLET_POPPED_BUBBLE, this)) {
      EventBus.get().unSubscribe(TURRET_BULLET_POPPED_BUBBLE, this);
    }
  }

  private void destroyBullet() {
    if (!isDestroyed) {
      get(BulletExplosionsEntity.class).explode(
          bulletSprite.getX() + bulletSprite.getWidthScaled() / 2,
          bulletSprite.getY() + bulletSprite.getHeightScaled() / 2);
      get(TurretBulletSpritePool.class).recycle(bulletSprite);
      destroyBulletReferences();
    }
  }

  private void destroyBulletReferences() {
    if (!isDestroyed) {
      isDestroyed = true;

      unregisterUpdateHandlers();
      physicsWorld.destroyBody(targetingMouseJoint.getBodyA());
      physicsWorld.destroyJoint(targetingMouseJoint);

      targetBubble = null;
      targetingMouseJoint = null;
      bulletSprite = null;
      bulletBody = null;
    }
  }
}
