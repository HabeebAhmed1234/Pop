package com.stupidfungames.pop.eventbus;

public class InteractionScoreEventPayload implements EventPayload {

    public final int interactionScore;

    public InteractionScoreEventPayload(int interactionScore) {
        this.interactionScore = interactionScore;
    }
}
