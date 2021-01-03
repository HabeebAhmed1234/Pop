package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubbletimeout.BubbleAboutToExplodeParticleSystemPoolEntity.Params;
import com.stupidfungames.pop.particles.BaseParticleSystemPool;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.sprite.Sprite;

class BubbleAboutToExplodeParticleSystemPoolEntity extends
    BaseParticleSystemPool<Params> {

  public BubbleAboutToExplodeParticleSystemPoolEntity(
      BinderEnity parent) {
    super(parent);
  }

  public static class Params extends BaseParticleSystemPoolParams {

    public final Sprite sprite;

    public Params(Sprite sprite) {
      super(sprite.getCenterX(), sprite.getCenterY());
      this.sprite = sprite;
    }
  }

  private static final float RATE_MIN = 15;
  private static final float RATE_MAX = 20;
  private static final int PARTICLES_MAX = 200;
  private static final float PARTICLE_DURATION_SECONDS = 0.3f;

  @Override
  protected void updateParticleSystem(ParticleSystem particleSystem,
      IParticleEmitter particleEmitter, Params params) {
    ((BubbleAboutToExplodeParticleEmitter) particleEmitter).setSprite(params.sprite);
  }

  @Override
  protected TextureId getParticleTexture() {
    return TextureId.BULLET;
  }

  @Override
  protected float getRateMin() {
    return RATE_MIN;
  }

  @Override
  protected float getRateMax() {
    return RATE_MAX;
  }

  @Override
  protected int getParticlesMax() {
    return PARTICLES_MAX;
  }

  @Override
  protected float getParticleDurationSeconds() {
    return PARTICLE_DURATION_SECONDS;
  }

  @Override
  protected IParticleEmitter createNewParticleEmitter(Params params) {
    return new BubbleAboutToExplodeParticleEmitter(params.sprite);
  }
}
