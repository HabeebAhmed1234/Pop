package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.entitymatchers.NeighboringBubblesEntityMatcher;
import com.stupidfungames.pop.eventbus.BubbleTouchedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
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
      // First mark all the bubbles on the screen
      List<IEntity> allBubbles = scene.query(new BubblesEntityMatcher(false, true));
      // Recursively pop only the marked bubbles
      BubbleTouchedEventPayload touchedEventPayload = (BubbleTouchedEventPayload) payload;
      popRecursively(touchedEventPayload.sprite);
    }
  }

  private void popRecursively(Sprite toPop) {
    bubblePopperEntity.popBubble(toPop);
    List<IEntity> neighboringBubbles = scene.query(new NeighboringBubblesEntityMatcher(
        toPop.getWidthScaled() * BUBBLE_EDGE_SEARCH_RADIUS_PERCENT, toPop));
    for (IEntity bubble : neighboringBubbles) {
      popRecursively((Sprite) bubble);
    }
  }
}
