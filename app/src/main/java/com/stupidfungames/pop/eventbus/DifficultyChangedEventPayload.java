package com.stupidfungames.pop.eventbus;

public class DifficultyChangedEventPayload implements EventPayload {

    public final float newSpawnInterval;

    public DifficultyChangedEventPayload(float newSpawnInterval) {
        this.newSpawnInterval = newSpawnInterval;
    }
}
