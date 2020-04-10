package com.stupidfungames.pop.eventbus;

public class GameDifficultyAccelerationErrorPayload implements EventPayload{

    public final float accelerationError;

    public GameDifficultyAccelerationErrorPayload(float accelerationError) {
        this.accelerationError = accelerationError;
    }
}
