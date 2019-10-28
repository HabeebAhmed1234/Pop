package com.wack.pop2.eventbus;

import com.wack.pop2.EventPayload;

public class IncrementScoreEventPayload implements EventPayload {

    public final int incrementAmmount;

    public IncrementScoreEventPayload(int incrementAmmount) {
        this.incrementAmmount = incrementAmmount;
    }
}
