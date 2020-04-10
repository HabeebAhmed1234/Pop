package com.stupidfungames.pop.eventbus;

public class TurretBulletPoppedBubbleEventPayload implements EventPayload {

    public final int bulletId;

    public TurretBulletPoppedBubbleEventPayload(int bulletId) {
        this.bulletId = bulletId;
    }
}
