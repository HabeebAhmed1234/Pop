package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.turrets.BulletExplosionsParticleSystemPoolEntity.EXPLOSION_DURATION_SECONDS;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.ParticleSystem;

/**
 * Manages the turret bullet explosions
 */
public class BulletExplosionsEffectEntity extends BaseEntity {

  BulletExplosionsEffectEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(BulletExplosionsParticleSystemPoolEntity.class,
        new BulletExplosionsParticleSystemPoolEntity(this));
  }

  public void explode(float x, float y) {
    final BulletExplosionsParticleSystemPoolEntity pool = get(
        BulletExplosionsParticleSystemPoolEntity.class);
    final ParticleSystem system = pool.get(new BaseParticleSystemPoolParams(x, y));
    engine.registerUpdateHandler(new TimerHandler(EXPLOSION_DURATION_SECONDS, new ITimerCallback() {
      @Override
      public void onTimePassed(TimerHandler pTimerHandler) {
        pool.recycle(system);
      }
    }));
  }
}
