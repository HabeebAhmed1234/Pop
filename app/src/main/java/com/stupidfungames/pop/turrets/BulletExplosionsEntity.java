package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.particles.ParticleSystemPool;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;

/**
 * Manages the turret bullet explosions
 */
public class BulletExplosionsEntity extends BaseEntity {

    private static final float EXPLOSION_DURATION_SECONDS = 0.3f;
    private static final float FADE_OUT_TIME = EXPLOSION_DURATION_SECONDS;

    private static final float RATE_MIN    = 15;
    private static final float RATE_MAX	   = 20;
    private static final int PARTICLES_MAX = 200;

    private static final int PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE = 500;

    BulletExplosionsEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(ParticleSystemPool.class, new ParticleSystemPool(
            new ParticleSystemPool.ParticleSystemFactory() {
                @Override
                public ParticleSystem create(float x, float y) {
                    IParticleEmitter emitter = new PointParticleEmitter(x,y);
                    ParticleSystem particleSystem = new BatchedSpriteParticleSystem(
                            emitter,
                            RATE_MIN,
                            RATE_MAX,
                            PARTICLES_MAX,
                            get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
                            vertexBufferObjectManager);

                    particleSystem.addParticleInitializer(getInitializer());
                    particleSystem.addParticleInitializer(new ExpireParticleInitializer(EXPLOSION_DURATION_SECONDS));
                    particleSystem.addParticleModifier(new AlphaParticleModifier(0, FADE_OUT_TIME, 1, 0));
                    return particleSystem;
                }
            }));
    }

    public void explode(float x, float y) {
        final ParticleSystemPool particleSystemPool = get(ParticleSystemPool.class);
        final ParticleSystem system = particleSystemPool.obtain(scene, x, y);
        applyVelocity(system);
        engine.registerUpdateHandler(new TimerHandler(EXPLOSION_DURATION_SECONDS, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                particleSystemPool.recycle(system);
            }
        }));
    }

    private void applyVelocity(ParticleSystem system) {
        system.removeParticleInitializers(VelocityParticleInitializer.class);
        system.addParticleInitializer(new VelocityParticleInitializer(
                -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE));
    }

    private VelocityParticleInitializer getInitializer() {
        return new VelocityParticleInitializer(
                -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                -PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE,
                PARTICLE_EXPLOSION_VELOCITY_MAGNITUDE);
    }
}
