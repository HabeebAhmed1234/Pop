package com.wack.pop2.eventbus;

import com.wack.pop2.BubbleSpawnerEntity;

public class StartingBubbleSpawnedEventPayload implements EventPayload {

    public final BubbleSpawnerEntity.BubbleType bubbleType;

    public StartingBubbleSpawnedEventPayload(BubbleSpawnerEntity.BubbleType bubbleType) {
        this.bubbleType = bubbleType;
    }
}
