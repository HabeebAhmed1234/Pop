package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubbletimeout.BubbleAboutToExplodeParticleSystemPoolEntity.Params;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.sprite.Sprite;

public class BubbleAboutToExplodeParticleEffectEntity extends BaseEntity {

  public BubbleAboutToExplodeParticleEffectEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BubbleAboutToExplodeParticleSystemPoolEntity.class,
        new BubbleAboutToExplodeParticleSystemPoolEntity(this));
  }

  public ParticleSystem start(Sprite sprite) {
    final BubbleAboutToExplodeParticleSystemPoolEntity pool = get(
        BubbleAboutToExplodeParticleSystemPoolEntity.class);
    return pool.get(new Params(sprite));
  }

  public void stop(ParticleSystem system) {
    get(BubbleAboutToExplodeParticleSystemPoolEntity.class).recycle(system);
  }
}
