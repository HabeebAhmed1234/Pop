package com.wack.pop2.bubbletimeout;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.BubbleSpawnedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;

import org.andengine.entity.IEntity;
import org.andengine.entity.OnDetachedListener;
import org.andengine.entity.sprite.Sprite;

import java.util.HashMap;
import java.util.Map;

public class BubblesLifecycleManagerEntity extends BaseEntity implements EventBus.Subscriber {


    private Map<Sprite, BubbleLifecycleControllersManager> controllersManagerMap = new HashMap<>();

    public BubblesLifecycleManagerEntity(GameResources gameResources) {
        super(gameResources);
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
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.BUBBLE_SPAWNED) {
            BubbleSpawnedEventPayload bubbleSpawnedEventPayload = (BubbleSpawnedEventPayload) payload;
            final Sprite bubbleSprite = bubbleSpawnedEventPayload.bubbleSprite;
            controllersManagerMap.put(
                    bubbleSprite,
                    new BubbleLifecycleControllersManager(
                            engine,
                            bubbleSprite));
            bubbleSprite.addOnDetachedListener(new OnDetachedListener() {
                @Override
                public void onDetached(IEntity entity) {
                    BubbleLifecycleControllersManager controller = controllersManagerMap.get(bubbleSprite);
                    controller.onDestroy();
                    bubbleSprite.removeOnDetachedListener(this);
                }
            });
        }
    }
}
