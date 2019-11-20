package com.wack.pop2.eventbus;

public class DifficultyChangedEventPayload implements EventPayload {

    public final int newDifficulty;

    public DifficultyChangedEventPayload(int newDifficulty) {
        this.newDifficulty = newDifficulty;
    }
}
