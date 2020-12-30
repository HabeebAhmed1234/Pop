package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.bombbubbles.BombBubbleExplosionsParticleSystemPool.BombBubbleExplosionParticlSystemPoolParams;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.ParticleSystem;

/**
 * Used for the explosion effect when the user diffuses a bomb bubble.
 */
public class BombBubbleExplosionEffectEntity extends BaseEntity {


  public BombBubbleExplosionEffectEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BombBubbleExplosionsParticleSystemPool.class,
        new BombBubbleExplosionsParticleSystemPool(this));
  }

  public void explode(float x, float y, float radiusPx) {
    final BombBubbleExplosionsParticleSystemPool pool = get(
        BombBubbleExplosionsParticleSystemPool.class);
    final ParticleSystem system = pool
        .get(new BombBubbleExplosionParticlSystemPoolParams(x, y, radiusPx));
    engine.registerUpdateHandler(
        new TimerHandler(BombBubbleExplosionsParticleSystemPool.EXPLOSION_DURATION_SECONDS,
            new ITimerCallback() {
              @Override
              public void onTimePassed(TimerHandler pTimerHandler) {
                pool.recycle(system);
              }
            }));
  }
}
