package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.BaseEntity;
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
import java.util.List;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

/**
 * Endgame tool that listens to the BUBBLE_TOUCHED event and pops all the bubbles around it
 * recursively.
 */
public class RecursiveBubblePopper extends BaseEntity implements Subscriber {

  private final float BUBBLE_EDGE_SEARCH_RADIUS_PERCENT = 0.2f;
  private BubblePopperEntity bubblePopperEntity;

  public RecursiveBubblePopper(BinderEnity parent) {
    super(parent);
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
      // First mark all the bubbles on the screen
      markAllBubblesOfSameType(touchedEventPayload.type);
      // Recursively pop only the marked bubbles
      popRecursively(touchedEventPayload.sprite, touchedEventPayload.type);
    }
  }

  private void markAllBubblesOfSameType(BubbleType type) {
    List<IEntity> allBubbles = scene.query(new SameBubblesTypeEntityMatcher(false, true, type));
    for (IEntity bubble : allBubbles) {
      ((BubbleEntityUserData) bubble.getUserData()).isMarkedForRecursivePopping = true;
    }
  }

  private void popRecursively(Sprite toPop, BubbleType type) {
    bubblePopperEntity.popBubble(toPop);
    List<IEntity> neighboringBubbles = scene
        .query(new NeighboringBubblesMarkedForRecursivePoppingEntityMatcher(
            toPop.getWidthScaled() * BUBBLE_EDGE_SEARCH_RADIUS_PERCENT, toPop, type));
    for (IEntity bubble : neighboringBubbles) {
      popRecursively((Sprite) bubble, type);
    }
  }
}
