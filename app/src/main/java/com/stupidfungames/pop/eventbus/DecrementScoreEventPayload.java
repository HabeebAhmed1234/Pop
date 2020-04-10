package com.stupidfungames.pop.eventbus;

public class DecrementScoreEventPayload implements EventPayload {

    public final int decrementAmmount;

    public DecrementScoreEventPayload(int decrementAmmount) {
        this.decrementAmmount = decrementAmmount;
    }
}
