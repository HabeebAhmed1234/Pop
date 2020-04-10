package com.stupidfungames.pop.eventbus;

public class IncrementScoreEventPayload implements EventPayload {

    public final int incrementAmmount;

    public IncrementScoreEventPayload(int incrementAmmount) {
        this.incrementAmmount = incrementAmmount;
    }
}
