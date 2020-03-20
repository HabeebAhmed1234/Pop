package com.wack.pop2.eventbus;

import com.wack.pop2.BubbleSpawnerEntity;

import org.andengine.entity.sprite.Sprite;

public class BubbleTouchedEventPayload implements EventPayload  {

    public final Sprite sprite;
    public final BubbleSpawnerEntity.BubbleSize size;
    public final BubbleSpawnerEntity.BubbleType type;

    public BubbleTouchedEventPayload(Sprite sprite, BubbleSpawnerEntity.BubbleSize size, BubbleSpawnerEntity.BubbleType type) {
        this. sprite = sprite;
        this.size = size;
        this.type = type;
    }
}
