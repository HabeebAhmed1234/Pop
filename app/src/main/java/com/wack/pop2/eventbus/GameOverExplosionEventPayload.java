package com.wack.pop2.eventbus;

import com.wack.pop2.EventPayload;

public class GameOverExplosionEventPayload implements EventPayload {

    public final float x;
    public final float y;
    public final float bubbleWidth;
    public final float bubbleHeight;
    public final float scale;

    public GameOverExplosionEventPayload(
            float x,
            float y,
            float bubbleWidth,
            float bubbleHeight,
            float scale) {
        this.x = x;
        this.y = y;
        this.bubbleWidth = bubbleWidth;
        this.bubbleHeight = bubbleHeight;
        this.scale = scale;
    }
}
