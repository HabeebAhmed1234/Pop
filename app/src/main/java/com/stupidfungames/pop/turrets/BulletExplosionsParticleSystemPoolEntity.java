package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.particles.BaseParticleSystemPool;
import com.stupidfungames.pop.particles.BaseParticleSystemPoolParams;
import com.stupidfungames.pop.resources.textures.TextureId;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.initializer.IParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;

public class BulletExplosionsParticleSystemPoolEntity extends
    BaseParticleSystemPool<BaseParticleSystemPoolParams> {

  public static final float EXPLOSION_DURATION_SECONDS = 0.3f;

  private static final float RATE_MIN = 15;
  private static final float RATE_MAX = 20;
  private static final int PARTICLES_MAX = 200;

  private static final int PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE = 500;

  public BulletExplosionsParticleSystemPoolEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void updateParticleSystem(ParticleSystem particleSystem,
      IParticleEmitter particleEmitter, BaseParticleSystemPoolParams params) {

  }

  @Override
  protected List<IParticleInitializer> getParticleInitializers(BaseParticleSystemPoolParams p) {
    List<IParticleInitializer> initializers = new ArrayList<>();
    initializers.add(new VelocityParticleInitializer(
        -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
        PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE));
    return initializers;
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
    return EXPLOSION_DURATION_SECONDS;
  }
}
