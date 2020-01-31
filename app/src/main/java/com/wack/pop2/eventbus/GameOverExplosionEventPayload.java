package com.wack.pop2.eventbus;

import org.andengine.entity.sprite.Sprite;

public class GameOverExplosionEventPayload implements EventPayload {

    /**
     * The bubble which exploded
     */
    public final Sprite bubble;

    public GameOverExplosionEventPayload(Sprite bubble) {
        this.bubble = bubble;
    }
}
