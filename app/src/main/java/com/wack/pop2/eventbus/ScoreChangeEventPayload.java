package com.wack.pop2.eventbus;

import com.wack.pop2.EventPayload;

public class ScoreChangeEventPayload implements EventPayload {

    public final int score;

    public ScoreChangeEventPayload(int score) {
        this.score = score;
    }
}
