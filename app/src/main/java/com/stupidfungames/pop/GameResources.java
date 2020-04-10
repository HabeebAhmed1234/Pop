package com.stupidfungames.pop;

import android.hardware.SensorManager;

import com.stupidfungames.pop.physics.PhysicsWorld;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.jbox2d.common.Vec2;

/**
 * Contains all of the resources required to interface with the andengine and physics api.
 * This includes the textures, physics engine, and scene ect.
 */
public class GameResources {

    public final Scene scene;
    public final PhysicsWorld physicsWorld;
    public final VertexBufferObjectManager vertexBufferObjectManager;
    public final Engine engine;
    public final HostActivityInterface hostActivity;

    public GameResources(
            Scene scene,
            PhysicsWorld physicsWorld,
            VertexBufferObjectManager vertexBufferObjectManager,
            Engine engine,
            HostActivityInterface hostActivity) {
        this.scene = scene;
        this.physicsWorld = physicsWorld;
        this.vertexBufferObjectManager = vertexBufferObjectManager;
        this.engine = engine;
        this.hostActivity = hostActivity;
    }

    /**
     * Initializes the game resources given their dependencies and returns a new instance.
     * @return
     */
    public static GameResources createNew(BaseGameActivity baseGameActivity, HostActivityInterface hostActivity) {
        Scene scene = new Scene();
        PhysicsWorld physicsWorld = new PhysicsWorld(new Vec2(0, SensorManager.GRAVITY_EARTH * 5), false);
        scene.registerUpdateHandler(physicsWorld);
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.setOnAreaTouchTraversalFrontToBack();
        return new GameResources(
                scene,
                physicsWorld,
                baseGameActivity.getVertexBufferObjectManager(),
                baseGameActivity.getEngine(), hostActivity);
    }
}
