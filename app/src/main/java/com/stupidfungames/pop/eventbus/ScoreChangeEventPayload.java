package com.stupidfungames.pop.eventbus;

public class ScoreChangeEventPayload implements EventPayload {

    public final int score;

    public ScoreChangeEventPayload(int score) {
        this.score = score;
    }
}
