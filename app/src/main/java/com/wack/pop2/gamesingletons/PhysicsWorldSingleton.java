package com.wack.pop2.gamesingletons;

import android.hardware.SensorManager;
import com.wack.pop2.physics.PhysicsWorld;
import org.jbox2d.common.Vec2;

import javax.inject.Scope;
import javax.inject.Singleton;

@Scope
@Singleton
public class PhysicsWorldSingleton {

    private static PhysicsWorld sWorld;

    public static PhysicsWorld instanceOf() {
        if (sWorld == null) {
            sWorld = new PhysicsWorld(new Vec2(0, SensorManager.GRAVITY_EARTH), false);
            SceneSingleton.instanceOf().registerUpdateHandler(sWorld);
        }
        return sWorld;
    }
}
