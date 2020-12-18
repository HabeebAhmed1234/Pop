package com.stupidfungames.pop.bubblepopper;

import static com.stupidfungames.pop.bubblepopper.BubblePopParticleSystemPool.PARTICLE_DURATION_SECONDS;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopParticleSystemPool.BubblePopParticleSystemPoolParams;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.ParticleSystem;

public class BubblePopParticleEffectEntity extends BaseEntity {

  public BubblePopParticleEffectEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BubblePopParticleSystemPool.class, new BubblePopParticleSystemPool(this));
  }


  public void emit(float x, float y, BubbleType bubbleType, float bubbleRadius) {
    final BubblePopParticleSystemPool particleSystemPool = get(BubblePopParticleSystemPool.class);
    final ParticleSystem particleSystem = particleSystemPool
        .get(new BubblePopParticleSystemPoolParams(x, y, bubbleRadius, bubbleType.color));
    engine.registerUpdateHandler(new TimerHandler(PARTICLE_DURATION_SECONDS, new ITimerCallback() {
      @Override
      public void onTimePassed(TimerHandler pTimerHandler) {
        particleSystemPool.recycle(particleSystem);
      }
    }));
  }
}
