package com.wack.pop2.eventbus;

import com.wack.pop2.EventPayload;

public class DecrementScoreEventPayload implements EventPayload {

    public final int decrementAmmount;

    public DecrementScoreEventPayload(int decrementAmmount) {
        this.decrementAmmount = decrementAmmount;
    }
}
