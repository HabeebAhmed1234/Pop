package com.stupidfungames.pop.upgrades;

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
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.IParticleModifier;
import org.andengine.util.color.AndengineColor;

public class UpgradeParticleSystemPoolEntity extends
    BaseParticleSystemPool<BaseParticleSystemPoolParams> {

  public static final float EXPLOSION_DURATION_SECONDS = 0.5f;

  private static final float RATE_MIN = 30;
  private static final float RATE_MAX = 50;
  private static final int PARTICLES_MAX = 300;

  private static final int PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE = 700;

  public UpgradeParticleSystemPoolEntity(BinderEnity parent) {
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
  protected List<IParticleModifier> getParticleModifiers() {
    List<IParticleModifier> modifiers = new ArrayList<>();
    modifiers.add(
        new ColorParticleModifier(0, EXPLOSION_DURATION_SECONDS, AndengineColor.WHITE,
            AndengineColor.YELLOW));
    return modifiers;
  }

  @Override
  protected TextureId getParticleTexture() {
    return TextureId.STAR;
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
