package com.stupidfungames.pop.bubblepopper;

import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_POPPED;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameAnimationManager;
import com.stupidfungames.pop.GameAnimationManager.AnimationListener;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleSize;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool;
import com.stupidfungames.pop.eventbus.BubblePoppedEventPayload;
import com.stupidfungames.pop.eventbus.BubbleTouchedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IncrementScoreEventPayload;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.pool.SpriteInitializerParams;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.utils.BubblePhysicsUtil;
import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class BubblePopperEntity extends BaseEntity {

  public static final int SCORE_INCREMENT_PER_BUBBLE_POP = 10;
  public static final int MAX_SCORE_INCREASE_PER_NEW_SPAWNED_BUBBLE = 70; // Since there are three bubble sizes a total of 7 bubbles can be popped from one spawned bubble
  private static final float POPPED_BUBBLES_HORIZONTAL_OFFSET = 0.5f;

  public BubblePopperEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(ScoreTickerSpritePool.class, new ScoreTickerSpritePool(this));
  }

  /**
   * Pops the given bubble. Returns true if bubble was popped false otherwise.
   */
  public boolean popBubble(Sprite previousBubble) {
    if (!previousBubble.isVisible() && previousBubble
        .getUserData() instanceof BubbleEntityUserData) {
      Log.d("BubblePopperEntity", "tried to pop the same bubble more than once");
      return false;
    }

    BubbleEntityUserData userData = (BubbleEntityUserData) previousBubble.getUserData();
    final BubbleSize oldBubbleSize = userData.size;
    final BubbleType bubbleType = userData.bubbleType;

    // Play the pop sound
    getRandomPopSound().play();

    Vec2 oldBubbleScenePosition = Vec2Pool.obtain(previousBubble.getX(), previousBubble.getY());
    // Spawn new bubbles if the one we popped not the smallest bubble
    if (!oldBubbleSize.isSmallestBubble()) {
      spawnPoppedBubbles(previousBubble, oldBubbleSize, oldBubbleScenePosition, bubbleType);
    }

    // Increment the score
    increaseScore(oldBubbleScenePosition.x, oldBubbleScenePosition.y);

    // Notify bubble popped
    EventBus.get().sendEvent(BUBBLE_POPPED,
        new BubblePoppedEventPayload(((BaseEntityUserData) previousBubble.getUserData()).getId(),
            oldBubbleScenePosition.x, oldBubbleScenePosition.y));

    Vec2Pool.recycle(oldBubbleScenePosition);
    // Remove the popped bubble
    get(BubbleSpritePool.class).recycle(previousBubble);
    return true;
  }

  /**
   * Spawns 2 new bubbles in the place where the old bubble was
   */
  private void spawnPoppedBubbles(
      final Sprite oldBubbleSprite,
      final BubbleSpawnerEntity.BubbleSize oldBubbleSize,
      final Vec2 oldBubbleScenePosition,
      final BubbleSpawnerEntity.BubbleType bubbleType) {
    BubbleSpawnerEntity bubbleSpawnerEntity = get(BubbleSpawnerEntity.class);

    float horizontalOffset =
        (oldBubbleSprite.getWidthScaled() / 2) * POPPED_BUBBLES_HORIZONTAL_OFFSET;

    Body leftBubble = bubbleSpawnerEntity.spawnBubble(
        bubbleType,
        oldBubbleScenePosition.x - horizontalOffset,
        oldBubbleScenePosition.y,
        oldBubbleSize.nextPoppedSize());

    BubblePhysicsUtil.applyVelocity(leftBubble, -3f, -1.2f);

    Body rightBubble = bubbleSpawnerEntity.spawnBubble(
        bubbleType,
        oldBubbleScenePosition.x + horizontalOffset,
        oldBubbleScenePosition.y,
        oldBubbleSize.nextPoppedSize());

    BubblePhysicsUtil.applyVelocity(rightBubble, 3f, -1.2f);
  }

  private void increaseScore(float sceneX, float sceneY) {
    showScoretickerText(sceneX, sceneY);
    EventBus.get().sendEvent(GameEvent.INCREMENT_SCORE,
        new IncrementScoreEventPayload(SCORE_INCREMENT_PER_BUBBLE_POP));
  }

  private void showScoretickerText(float x, float y) {
    final ScoreTickerSpritePool scoreTickerSpritePool = get(ScoreTickerSpritePool.class);
    final Text scorePlus10Text = (Text) scoreTickerSpritePool
        .get(new SpriteInitializerParams(x, y));
    if (!scorePlus10Text.isAttached()) {
      addToScene(scorePlus10Text);
    }
    ParallelEntityModifier animation = new ParallelEntityModifier(
        new ScaleModifier(0.75f, 0.1f, 1.1f),
        new AlphaModifier(0.75f, 1f, 0f));
    get(GameAnimationManager.class).startModifier(scorePlus10Text, animation,
        new AnimationListener() {
          @Override
          public void onFinished() {
            scoreTickerSpritePool.recycle(scorePlus10Text);
          }
        });
  }

  private Sound getRandomPopSound() {
    int random = (int) (Math.random() * 4);
    GameSoundsManager soundsManager = get(GameSoundsManager.class);
    switch (random) {
      case 0:
        return soundsManager.getSound(SoundId.POP_1);
      case 1:
        return soundsManager.getSound(SoundId.POP_2);
      case 2:
        return soundsManager.getSound(SoundId.POP_3);
      case 3:
        return soundsManager.getSound(SoundId.POP_4);
      case 4:
        return soundsManager.getSound(SoundId.POP_5);
    }
    throw new IllegalStateException("No sound for index " + random);
  }
}
