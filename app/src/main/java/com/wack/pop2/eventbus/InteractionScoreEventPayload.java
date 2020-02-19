package com.wack.pop2.eventbus;

public class InteractionScoreEventPayload implements EventPayload {

    public final int interactionScore;

    public InteractionScoreEventPayload(int interactionScore) {
        this.interactionScore = interactionScore;
    }
}
