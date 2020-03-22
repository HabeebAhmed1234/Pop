package com.wack.pop2.eventbus;

public class GameProgressEventPayload implements EventPayload {

    // Number between 0 and 1
    public final float percentProgress;

    public GameProgressEventPayload(float percentProgress) {
        this.percentProgress = percentProgress;
    }
}
