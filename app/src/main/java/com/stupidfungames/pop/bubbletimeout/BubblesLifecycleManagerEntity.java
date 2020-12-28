package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.BubbleSpawnedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import java.util.HashMap;
import java.util.Map;
import org.andengine.entity.sprite.Sprite;

public class BubblesLifecycleManagerEntity extends BaseEntity implements EventBus.Subscriber {

  private Map<Sprite, BubbleLifecycleControllersManager> controllersManagerMap = new HashMap<>();

  public BubblesLifecycleManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get().subscribe(GameEvent.BUBBLE_SPAWNED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(GameEvent.BUBBLE_SPAWNED, this);
    for (BubbleLifecycleControllersManager manager : controllersManagerMap.values()) {
      manager.onLifeycleControllersDestroy();
    }
    controllersManagerMap.clear();
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    if (event == GameEvent.BUBBLE_SPAWNED) {
      BubbleSpawnedEventPayload bubbleSpawnedEventPayload = (BubbleSpawnedEventPayload) payload;
      final Sprite bubbleSprite = bubbleSpawnedEventPayload.bubbleSprite;
      if (controllersManagerMap.containsKey(bubbleSprite)) {
        controllersManagerMap.get(bubbleSprite).reset();
      } else {
        controllersManagerMap.put(
            bubbleSprite,
            new BubbleLifecycleControllersManager(
                get(GameSoundsManager.class),
                engine,
                bubbleSprite,
                this));
      }
    }
  }
}
