package com.stupidfungames.pop.eventbus;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;

public class StartingBubbleSpawnedEventPayload implements EventPayload {

    public final BubbleSpawnerEntity.BubbleType bubbleType;

    public StartingBubbleSpawnedEventPayload(BubbleSpawnerEntity.BubbleType bubbleType) {
        this.bubbleType = bubbleType;
    }
}
