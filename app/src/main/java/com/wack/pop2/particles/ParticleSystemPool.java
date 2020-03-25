package com.wack.pop2.particles;

import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.scene.Scene;
import org.andengine.util.adt.list.ShiftList;
import org.andengine.util.adt.queue.IQueue;
import org.andengine.util.adt.queue.concurrent.SynchronizedQueue;
import org.jbox2d.common.Vec2;

/**
 * Pool of particle systems that ensures that they are paused before setting them for reuse.
 * New systems are automatically added to the scene.
 */
public class ParticleSystemPool {

    public interface ParticleSystemFactory {
        ParticleSystem create(float x, float y);
    }

    private final ParticleSystemFactory factory;
    private final IQueue<ParticleSystem> pool = new SynchronizedQueue(new ShiftList());

    public ParticleSystemPool(ParticleSystemFactory factory) {
        this.factory = factory;
    }

    public void recycle(ParticleSystem system) {
        system.setParticlesSpawnEnabled(false);
    }

    public ParticleSystem obtain(Scene scene, float x, float y) {
        if (!pool.isEmpty()) {
            ParticleSystem system = pool.poll();
            system.setX(x);
            system.setY(y);
            system.reset();
            system.setParticlesSpawnEnabled(true);
            return system;
        }
        ParticleSystem system = factory.create(x, y);
        scene.attachChild(system);
        return system;
    }
}
