package com.wack.pop2.eventbus;

public class GameScoreAccelerationChangedPayload implements EventPayload {

    public final float scoreAcceleration;

    public GameScoreAccelerationChangedPayload(float scoreAcceleration) {
        this.scoreAcceleration = scoreAcceleration;
    }
}
