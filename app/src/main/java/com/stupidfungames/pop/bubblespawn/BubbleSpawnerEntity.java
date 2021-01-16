package com.stupidfungames.pop.bubblespawn;

import static com.stupidfungames.pop.GameConstants.MAX_BUBBLES_ON_SCREEN;
import static com.stupidfungames.pop.GameConstants.MAX_BUBBLES_PER_SPAWN_AT_MAX_DIFFICULTY;
import static com.stupidfungames.pop.GameConstants.MAX_BUBBLES_PER_SPAWN_AT_MIN_DIFFICULTY;
import static com.stupidfungames.pop.GameConstants.MAX_SPAWN_INTERVAL_SECONDS;
import static com.stupidfungames.pop.GameConstants.MIN_SPAWN_INTERVAL_SECONDS;
import static org.andengine.util.math.MathUtils.RANDOM;

import android.hardware.SensorManager;
import android.util.Log;
import android.util.Pair;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.BubbleTouchFactoryEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool.BubbleSpritePoolParams;
import com.stupidfungames.pop.bubblespawn.bombbubbles.BombBubbleSpawnerEntity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.eventbus.BubbleSpawnedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameDifficultyEventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IconUnlockedEventPayload;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.utils.BubblePhysicsUtil;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class BubbleSpawnerEntity extends BaseEntity implements EventBus.Subscriber {

  public static final float BUBBLE_GRAVITY_SCALE = 0.5f;
  // Value to define how much smaller as a percentage the bubble physics circle will be from the sprite
  public static final float BUBBLE_BODY_SCALE_FACTOR = 0.8f;

  public enum BubbleType {
    RED(AndengineColor.RED),
    GREEN(AndengineColor.GREEN),
    BLUE(AndengineColor.BLUE),
    CYAN(AndengineColor.CYAN),
    PINK(AndengineColor.PINK);

    private static final Random RANDOM = new Random();

    public final AndengineColor color;

    BubbleType(AndengineColor color) {
      this.color = color;
    }

    /**
     * Returns a random bubble type
     *
     * @param withExtraBubbles whether or not to include the extra 2 bubble types
     */
    public static BubbleType random(boolean withExtraBubbles) {
      int size = values().length;
      return values()[RANDOM.nextInt(withExtraBubbles ? size : size - 2)];
    }
  }

  public enum BubbleSize {
    LARGE(160),
    MEDIUM(120),
    SMALL(80);

    public final float sizeDp;
    private final Map<BubbleSize, Float> sizeToPxMap = new HashMap<>();

    BubbleSize(final float dp) {
      this.sizeDp = dp;
    }

    public float getSizePx() {
      if (!sizeToPxMap.containsKey(this)) {
        sizeToPxMap.put(this, (float) ScreenUtils.dpToPx(sizeDp));
      }
      return sizeToPxMap.get(this);
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

  private float bubbleSpawnIntervalSeconds = MAX_SPAWN_INTERVAL_SECONDS;
  private int maxBubblesPerSpawn = MAX_BUBBLES_PER_SPAWN_AT_MIN_DIFFICULTY;
  private boolean shouldIncludeExtraBubblesInSpawn = false;
  private TimerHandler bubbleSpawnTimerHandler = new TimerHandler(
      bubbleSpawnIntervalSeconds,
      false,
      new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          if (!isBubbleLimitReached()) {
            int numBubbles = RANDOM.nextInt(maxBubblesPerSpawn) + 1;
            if (numBubbles == 0) {
              numBubbles = 1;
            }
            Log.d("asdasd", "numBubbles = " + numBubbles);
            List<Pair<Float, Float>> startingBubblePositions = BubblePacker
                .getSpawnBubblesLocations(
                    numBubbles,
                    BubbleSize.LARGE.getSizePx());
            for (Pair<Float, Float> position : startingBubblePositions) {
              if (!get(BombBubbleSpawnerEntity.class)
                  .maybeSpawnBombBubble(position.first, position.second)) {
                spawnStartingBubble(position.first, position.second);
              }
            }
          }
          engine.registerUpdateHandler(new TimerHandler(bubbleSpawnIntervalSeconds, false, this));
        }
      });

  public BubbleSpawnerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    EventBus.get()
        .subscribe(GameEvent.GAME_DIFFICULTY_CHANGED, this, true)
        .subscribe(GameEvent.ICON_UNLOCKED, this, true);
    engine.registerUpdateHandler(bubbleSpawnTimerHandler);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BombBubbleSpawnerEntity.class, new BombBubbleSpawnerEntity(this));
  }

  @Override
  public void onDestroy() {
    EventBus.get()
        .unSubscribe(GameEvent.GAME_DIFFICULTY_CHANGED, this)
        .unSubscribe(GameEvent.ICON_UNLOCKED, this);
  }

  private boolean isBubbleLimitReached() {
    final int numBubbles = scene.query(new BubblesEntityMatcher(false, false)).size();
    return numBubbles > MAX_BUBBLES_ON_SCREEN;
  }

  private void spawnStartingBubble(final float x, final float y) {
    BubbleType bubbleType = BubbleType.random(shouldIncludeExtraBubblesInSpawn);
    Body body = spawnBubble(bubbleType, x, y, BubbleSize.LARGE);
    BubblePhysicsUtil.applyVelocity(body, 0f, (float) (SensorManager.GRAVITY_EARTH * 0.6));
  }

  /**
   * Creates a bubble in the scene and returns the corresponding physics body.
   *
   * returns the Body of the spawned bubble
   */
  public Body spawnBubble(final BubbleType bubbleType, final float x, final float y,
      BubbleSize bubbleSize) {
    //add object
    final Sprite bubbleSprite = (Sprite) get(BubbleSpritePool.class)
        .get(new BubbleSpritePoolParams(x, y, bubbleType, bubbleSize));

    final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
    bubbleFixtureDef.setFilter(CollisionFilters.BUBBLE_FILTER);
    bubbleFixtureDef.setUserData(bubbleSprite.getUserData());
    final Body body = PhysicsFactory
        .createCircleBody(physicsWorld, bubbleSprite, BUBBLE_BODY_SCALE_FACTOR, BodyType.DYNAMIC,
            bubbleFixtureDef);
    body.setGravityScale(BUBBLE_GRAVITY_SCALE);
    addToSceneWithTouch(bubbleSprite, body,
        get(BubbleTouchFactoryEntity.class).getNewTouchBubblePopper());
    notifyBubbleSpawned(bubbleSprite);
    return body;
  }

  private void notifyBubbleSpawned(Sprite bubbleSprite) {
    EventBus.get().sendEvent(GameEvent.BUBBLE_SPAWNED, new BubbleSpawnedEventPayload(bubbleSprite));
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case GAME_DIFFICULTY_CHANGED:
        GameDifficultyEventPayload difficultyPayload = (GameDifficultyEventPayload) payload;
        setDifficultyParams(difficultyPayload.difficulty);
        break;
      case ICON_UNLOCKED:
        if (((IconUnlockedEventPayload) payload).unlockedIconId == IconId.MULTI_POP_ICON) {
          shouldIncludeExtraBubblesInSpawn = true;
        }
        break;
    }
  }

  private void setDifficultyParams(float difficulty) {
    bubbleSpawnIntervalSeconds = getSpawnIntervalFromDifficulty(difficulty);
    maxBubblesPerSpawn = getMaxBubblesPerSpawn(difficulty);
  }


  /**
   * Returns the spawn interval given the current game difficulty.
   */
  private float getSpawnIntervalFromDifficulty(float difficulty) {
    return MAX_SPAWN_INTERVAL_SECONDS
        - (MAX_SPAWN_INTERVAL_SECONDS - MIN_SPAWN_INTERVAL_SECONDS) * difficulty;
  }

  private int getMaxBubblesPerSpawn(float difficulty) {
    return (int) Math.floor(MAX_BUBBLES_PER_SPAWN_AT_MIN_DIFFICULTY
        + (MAX_BUBBLES_PER_SPAWN_AT_MAX_DIFFICULTY - MAX_BUBBLES_PER_SPAWN_AT_MIN_DIFFICULTY)
        * difficulty);
  }
}
