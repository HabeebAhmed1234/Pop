package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameOverExplosionEventPayload;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

public class BombBubbleExpiredListenerEntity extends BaseEntity implements
    BombBubbleExpiredListener {

  public BombBubbleExpiredListenerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onBombBubbleExpired(IEntity bombBubbleSprite) {
    if (bombBubbleSprite.isVisible()) {
      get(BombBubbleSpritePool.class).recycle(bombBubbleSprite);
    }
    EventBus.get().sendEvent(
        GameEvent.GAME_OVER_ON_EXPLOSION_EVENT,
        new GameOverExplosionEventPayload(
            Vec2Pool.obtain(((Sprite) bombBubbleSprite).getCenter())));
  }
}