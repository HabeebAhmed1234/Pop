package com.wack.pop2.eventbus;

public class TurretBulletPoppedBubbleEventPayload implements EventPayload {

    public final int bulletId;

    public TurretBulletPoppedBubbleEventPayload(int bulletId) {
        this.bulletId = bulletId;
    }
}
