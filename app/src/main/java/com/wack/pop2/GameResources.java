package com.wack.pop2;

import com.wack.pop2.physics.PhysicsWorld;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.scene.Scene;

/**
 * Contains all of the resources required to interface with the andengine and physics api.
 * This includes the textures, physics engine, and scene ect.
 */
public class GameResources {

    public final Scene scene;
    public final PhysicsWorld physicsWorld;
    public final GameTexturesManager gameTexturesManager;

    public GameResources(Scene scene, PhysicsWorld physicsWorld, GameTexturesManager gameTexturesManager) {
        this.scene = scene;
        this.physicsWorld = physicsWorld;
        this.gameTexturesManager = gameTexturesManager;
    }
}
