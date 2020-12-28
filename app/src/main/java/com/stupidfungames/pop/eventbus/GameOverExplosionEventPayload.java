package com.stupidfungames.pop.eventbus;

import org.jbox2d.common.Vec2;

public class GameOverExplosionEventPayload implements EventPayload {

  public final Vec2 explosionLocationCenter;

  public GameOverExplosionEventPayload(Vec2 explosionLocationCenter) {
    this.explosionLocationCenter = explosionLocationCenter;
  }
}
