package com.stupidfungames.pop.bubblespawn;

import static com.stupidfungames.pop.GameConstants.MAX_BUBBLES_ON_SCREEN;
import static com.stupidfungames.pop.GameConstants.MAX_BUBBLES_PER_SPAWN;
import static com.stupidfungames.pop.GameConstants.MAX_SPAWN_INTERVAL;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Pair;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.TouchPopperFactoryEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.eventbus.BubbleSpawnedEventPayload;
import com.stupidfungames.pop.eventbus.DifficultyChangedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.utils.BubblePhysicsUtil;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class BubbleSpawnerEntity extends BaseEntity implements EventBus.Subscriber {

  private static final float BUBBLE_GRAVITY_SCALE = 0.5f;
  // Value to define how much smaller as a percentage the bubble physics circle will be from the sprite
  private static final float BUBBLE_PHYSICS_BODY_SCALE_FACTOR = 0.8f;

  public enum BubbleType {
    RED,
    GREEN,
    BLUE;

    private static final List<BubbleType> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static BubbleType random() {
      return VALUES.get(RANDOM.nextInt(SIZE));
    }
  }

  public enum BubbleSize {
    LARGE(160),
    MEDIUM(120),
    SMALL(80);

    public final float sizeDp;

    BubbleSize(final float dp) {
      this.sizeDp = dp;
    }

    public BubbleSize nextPoppedSize() {
      int nextIndex = ordinal() + 1;
      if (nextIndex >= BubbleSize.values().length) {
        return values()[BubbleSize.values().length - 1];
      }
      return values()[nextIndex];
    }

    public boolean isSmallestBubble() {
      return ordinal() >= BubbleSize.values().length - 1;
    }
  }

  private float bubbleSpawnInterval = MAX_SPAWN_INTERVAL;
  private TimerHandler bubbleSpawnTimerHandler = new TimerHandler(
      bubbleSpawnInterval,
      false,
      new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          if (!isBubbleLimitReached()) {
            int numBubbles = (int) (Math.random() * MAX_BUBBLES_PER_SPAWN);
            if (numBubbles == 0) {
              numBubbles = 1;
            }
            List<Pair<Float, Float>> startingBubblePositions = BubblePacker
                .getSpawnBubblesLocations(
                    numBubbles,
                    ScreenUtils.dpToPx(BubbleSize.LARGE.sizeDp, get(Context.class)));
            for (Pair<Float, Float> position : startingBubblePositions) {
              spawnStartingBubble(position.first, position.second);
            }
          }
          engine.registerUpdateHandler(new TimerHandler(bubbleSpawnInterval, false, this));
        }
      });

  public BubbleSpawnerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    EventBus.get().subscribe(GameEvent.SPAWN_INTERVAL_CHANGED, this, true);
    engine.registerUpdateHandler(bubbleSpawnTimerHandler);
  }

  @Override
  public void onDestroy() {
    engine.unregisterUpdateHandler(bubbleSpawnTimerHandler);
    EventBus.get().unSubscribe(GameEvent.SPAWN_INTERVAL_CHANGED, this);
  }

  private boolean isBubbleLimitReached() {
    final int numBubbles = scene.query(new BubblesEntityMatcher(false, false)).size();
    return numBubbles > MAX_BUBBLES_ON_SCREEN;
  }

  private void spawnStartingBubble(final float x, final float y) {
    BubbleType bubbleType = BubbleType.random();
    Body body = spawnBubble(bubbleType, x, y, BubbleSize.LARGE);
    BubblePhysicsUtil.applyVelocity(body, 0f, (float) (SensorManager.GRAVITY_EARTH * 0.3 * 2));
  }

  /**
   * Creates a bubble in the scene and returns the corresponding physics connector.
   *
   * returns the Body of the spawned bubble
   */
  public Body spawnBubble(final BubbleType bubbleType, final float x, final float y,
      BubbleSize bubbleSize) {
    //add object
    BubbleSpritePool spritePool = get(BubbleSpritePool.class);
    spritePool.setNextBubbleMetaData(bubbleType, bubbleSize);
    final Sprite bubbleSprite = spritePool.createNewSprite(x, y);

    final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
    bubbleFixtureDef.setFilter(CollisionFilters.BUBBLE_FILTER);
    bubbleFixtureDef.setUserData(bubbleSprite.getUserData());
    final Body body = PhysicsFactory
        .createCircleBody(physicsWorld, bubbleSprite, 0.8f, BodyType.DYNAMIC, bubbleFixtureDef);
    body.setGravityScale(BUBBLE_GRAVITY_SCALE);
    addToSceneWithTouch(bubbleSprite, body,
        get(TouchPopperFactoryEntity.class).getNewTouchBubblePopper());
    notifyBubbleSpawned(bubbleSprite);
    return body;
  }

  private void notifyBubbleSpawned(Sprite bubbleSprite) {
    EventBus.get().sendEvent(GameEvent.BUBBLE_SPAWNED, new BubbleSpawnedEventPayload(bubbleSprite));
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == GameEvent.SPAWN_INTERVAL_CHANGED) {
      DifficultyChangedEventPayload difficultyChangedEventPayload = (DifficultyChangedEventPayload) payload;
      bubbleSpawnInterval = difficultyChangedEventPayload.newSpawnInterval;
    }
  }
}
