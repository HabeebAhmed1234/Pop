package com.stupidfungames.pop.eventbus;

public class GameDifficultyEventPayload implements EventPayload {

    // Number between 0 and 1
    public final float difficulty;

    public GameDifficultyEventPayload(float difficulty) {
        this.difficulty = difficulty;
    }
}
