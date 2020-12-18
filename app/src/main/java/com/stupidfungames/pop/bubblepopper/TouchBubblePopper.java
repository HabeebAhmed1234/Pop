package com.stupidfungames.pop.bubblepopper;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BUBBLE_BODY_SCALE_FACTOR;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import com.stupidfungames.pop.entitymatchers.NeighboringBubblesMarkedForRecursivePoppingEntityMatcher;
import com.stupidfungames.pop.entitymatchers.SameBubblesTypeEntityMatcher;
import com.stupidfungames.pop.eventbus.BubbleTouchedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

/**
 * Either an Endgame tool that listens to the BUBBLE_TOUCHED event and pops all the bubbles around
 * it recursively or single touch pop tool.
 */
public class TouchBubblePopper extends BaseEntity implements Subscriber {

  private final float BUBBLE_EDGE_SEARCH_RADIUS_PERCENT = 0.2f;
  private BubblePopperEntity bubblePopperEntity;
  private boolean isRecursiveEnabled = true;

  public TouchBubblePopper(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BubblePopParticleEffectEntity.class, new BubblePopParticleEffectEntity(this));
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    bubblePopperEntity = get(BubblePopperEntity.class);
    EventBus.get().subscribe(GameEvent.BUBBLE_TOUCHED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(GameEvent.BUBBLE_TOUCHED, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == GameEvent.BUBBLE_TOUCHED) {
      BubbleTouchedEventPayload touchedEventPayload = (BubbleTouchedEventPayload) payload;
      showBubbleTouchPopParticleEffect(touchedEventPayload);
      if (!isRecursiveEnabled) {
        bubblePopperEntity.popBubble(touchedEventPayload.sprite);
        return;
      }
      // First mark all the bubbles on the screen
      markAllBubblesOfSameType(touchedEventPayload.type);
      // Recursively finds all the bubbles in transitive contact with the given bubble and assembles
      // a list.
      List<Sprite> bubblesToPop = new ArrayList<>();
      recursivelyQueryAllBubblesInContact(bubblesToPop, touchedEventPayload.sprite,
          touchedEventPayload.type);
      for (Sprite bubble : bubblesToPop) {
        bubblePopperEntity.popBubble(bubble);
      }
    }
  }

  private void markAllBubblesOfSameType(BubbleType type) {
    List<IEntity> allBubbles = scene.query(new SameBubblesTypeEntityMatcher(false, true, type));
    for (IEntity bubble : allBubbles) {
      ((BubbleEntityUserData) bubble.getUserData()).isMarkedForRecursivePopping = true;
    }
  }

  private void recursivelyQueryAllBubblesInContact(List<Sprite> bubblesToPop, Sprite toPop,
      BubbleType type) {
    // Add this bubble to the poppable list
    bubblesToPop.add(toPop);
    // Make sure to unmark this for recursive popping
    BubbleEntityUserData userData = (BubbleEntityUserData) toPop.getUserData();
    userData.isMarkedForRecursivePopping = false;
    List<IEntity> neighboringBubbles = scene
        .query(new NeighboringBubblesMarkedForRecursivePoppingEntityMatcher(
            toPop.getWidthScaled() * BUBBLE_EDGE_SEARCH_RADIUS_PERCENT, toPop, type));
    for (IEntity bubble : neighboringBubbles) {
      recursivelyQueryAllBubblesInContact(bubblesToPop, (Sprite) bubble, type);
    }
  }

  private void showBubbleTouchPopParticleEffect(BubbleTouchedEventPayload touchedEventPayload) {
    float[] poppedBubbleCenter = touchedEventPayload.sprite.getCenter();
    get(BubblePopParticleEffectEntity.class)
        .emit(poppedBubbleCenter[0], poppedBubbleCenter[1],
            touchedEventPayload.type,
            touchedEventPayload.sprite.getWidthScaled() / 2 * BUBBLE_BODY_SCALE_FACTOR);
  }
}
