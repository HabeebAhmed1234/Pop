package com.wack.pop2.eventbus;

public class DifficultyChangedEventPayload implements EventPayload {

    public final float newSpawnInterval;

    public DifficultyChangedEventPayload(float newSpawnInterval) {
        this.newSpawnInterval = newSpawnInterval;
    }
}
