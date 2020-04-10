package com.stupidfungames.pop.eventbus;

public class GameScoreAccelerationChangedPayload implements EventPayload {

    public final float scoreAcceleration;

    public GameScoreAccelerationChangedPayload(float scoreAcceleration) {
        this.scoreAcceleration = scoreAcceleration;
    }
}
