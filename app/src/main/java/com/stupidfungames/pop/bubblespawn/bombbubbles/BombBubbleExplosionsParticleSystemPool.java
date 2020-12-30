package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.bubblespawn.bombbubbles.BombBubbleSpritePool.DIFFUSE_BOMB_COLOUR;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.bombbubbles.BombBubbleExplosionsParticleSystemPool.BombBubbleExplosionParticlSystemPoolParams;
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

public class BombBubbleExplosionsParticleSystemPool extends
    BaseParticleSystemPool<BombBubbleExplosionParticlSystemPoolParams> {

  public static final float EXPLOSION_DURATION_SECONDS = 0.5f;

  private static final float RATE_MIN = 50;
  private static final float RATE_MAX = 100;
  private static final int PARTICLES_MAX = 500;

  public static class BombBubbleExplosionParticlSystemPoolParams extends
      BaseParticleSystemPoolParams {

    public final float radiusPx;

    public BombBubbleExplosionParticlSystemPoolParams(float emitterX, float emitterY,
        float radiusPx) {
      super(emitterX, emitterY);
      this.radiusPx = radiusPx;
    }
  }

  public BombBubbleExplosionsParticleSystemPool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void updateParticleSystem(ParticleSystem particleSystem,
      IParticleEmitter particleEmitter, BombBubbleExplosionParticlSystemPoolParams params) {
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
  protected float getParticleDuration() {
    return EXPLOSION_DURATION_SECONDS;
  }

  @Override
  protected List<IParticleInitializer> getParticleInitializers(
      BombBubbleExplosionParticlSystemPoolParams p) {
    List<IParticleInitializer> initializers = new ArrayList<>();
    float xyVelocity = p.radiusPx / EXPLOSION_DURATION_SECONDS;
    initializers
        .add(new VelocityParticleInitializer(-xyVelocity, xyVelocity, -xyVelocity, xyVelocity));
    return initializers;
  }

  @Override
  protected List<IParticleModifier> getParticleModifiers() {
    List<IParticleModifier> modifiers = new ArrayList<>();
    modifiers.add(new ColorParticleModifier(0, EXPLOSION_DURATION_SECONDS, AndengineColor.WHITE, DIFFUSE_BOMB_COLOUR));
    return modifiers;
  }
}
