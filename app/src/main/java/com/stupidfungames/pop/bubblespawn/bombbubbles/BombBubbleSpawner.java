package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BUBBLE_GRAVITY_SCALE;
import static com.stupidfungames.pop.eventbus.GameEvent.GAME_PROGRESS_CHANGED;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameProgressEventPayload;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.pool.BaseSpriteInitializerParams;
import java.util.Random;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Gets delegated to by the {@link BubbleSpawnerEntity} to maybe spawn a BubbleSize.LARGE or smaller
 * bomb bubble in the given location.
 */
public class BombBubbleSpawner extends BaseEntity implements Subscriber {

  // Value to define how much smaller as a percentage the bubble physics circle will be from the sprite
  public static final float BOMB_BUBBLE_BODY_SCALE_FACTOR = 0.8f;
  // The probability of a bomb spawning at max difficulty
  public static final float MAX_BOMB_BUBBLE_PROBABILITY = 0.4f;
  // The probability of a bomb spawning at min difficulty
  public static final float MIN_BOMB_BUBBLE_PROBABILITY = 0.1f;

  private float currentBombBubbleSpawnChance = MIN_BOMB_BUBBLE_PROBABILITY;
  private final Random random = new Random();

  public BombBubbleSpawner(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BombBubbleSpritePool.class, new BombBubbleSpritePool(this));
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get().subscribe(GAME_PROGRESS_CHANGED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(GAME_PROGRESS_CHANGED, this);
  }

  public boolean maybeSpawnBombBubble(float x, float y) {
    if (random.nextFloat() < currentBombBubbleSpawnChance) {
      spawnBombBubble(x, y);
      return true;
    }
    return false;
  }

  private void spawnBombBubble(float x, float y) {
    final Sprite bubbleSprite = (Sprite) get(BombBubbleSpritePool.class)
        .get(new BaseSpriteInitializerParams(x, y));
    final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
    bubbleFixtureDef.setFilter(CollisionFilters.BOMB_BUBBLE_FILTER);
    bubbleFixtureDef.setUserData(bubbleSprite.getUserData());
    final Body body = PhysicsFactory
        .createCircleBody(physicsWorld, bubbleSprite, BOMB_BUBBLE_BODY_SCALE_FACTOR,
            BodyType.DYNAMIC,
            bubbleFixtureDef);
    body.setGravityScale(BUBBLE_GRAVITY_SCALE);
    addToScene(bubbleSprite, body);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == GAME_PROGRESS_CHANGED) {
      GameProgressEventPayload gameProgressEventPayload = (GameProgressEventPayload) payload;
      currentBombBubbleSpawnChance =
          (MAX_BOMB_BUBBLE_PROBABILITY - MIN_BOMB_BUBBLE_PROBABILITY)
              * gameProgressEventPayload.percentProgress + MIN_BOMB_BUBBLE_PROBABILITY;
    }
  }
}
