package com.stupidfungames.pop.upgrades;

import static com.stupidfungames.pop.upgrades.UpgradeParticleSystemPoolEntity.EXPLOSION_DURATION_SECONDS;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.ParticleSystem;

public class UpgradesParticleEffectEntity extends BaseEntity {

  public UpgradesParticleEffectEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(UpgradeParticleSystemPoolEntity.class, new UpgradeParticleSystemPoolEntity(this));
  }

  public void emit(float x, float y) {
    final UpgradeParticleSystemPoolEntity pool = get(UpgradeParticleSystemPoolEntity.class);
    final ParticleSystem system = pool.get(new BaseParticleSystemPoolParams(x, y));
    engine.registerUpdateHandler(new TimerHandler(EXPLOSION_DURATION_SECONDS, new ITimerCallback() {
      @Override
      public void onTimePassed(TimerHandler pTimerHandler) {
        pool.recycle(system);
      }
    }));
  }
}
