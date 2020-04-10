package com.stupidfungames.pop.eventbus;

import org.andengine.entity.sprite.Sprite;

public class BubbleSpawnedEventPayload implements EventPayload {

    public final Sprite bubbleSprite;

    public  BubbleSpawnedEventPayload(Sprite bubbleSprite) {
        this.bubbleSprite = bubbleSprite;
    }
}
