package com.wack.pop2.eventbus;

public class GameDifficultyAccelerationErrorPayload implements EventPayload{

    public final float accelerationError;

    public GameDifficultyAccelerationErrorPayload(float accelerationError) {
        this.accelerationError = accelerationError;
    }
}
