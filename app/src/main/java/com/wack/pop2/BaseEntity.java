package com.wack.pop2;

import com.wack.pop2.physics.PhysicsWorld;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * This class represents the base functionality of all entities within the game.
 *
 * An entity is a game object which has access to the game scene and physics world. All user inputs
 * will ping methods within game entities which will then update the underlying sprites.
 *
 * All data is stored within these entity objects that extends this BaseEntity
 */
public abstract class BaseEntity implements GameLifeCycleCalllbackManager.GameCallback {

    protected Scene scene;
    protected PhysicsWorld physicsWorld;
    protected VertexBufferObjectManager vertexBufferObjectManager;
    protected Engine engine;
    protected HostActivityInterface hostActivity;

    public BaseEntity(GameResources gameResources) {
        this.scene = gameResources.scene;
        this.physicsWorld = gameResources.physicsWorld;
        this.vertexBufferObjectManager = gameResources.vertexBufferObjectManager;
        this.engine = gameResources.engine;
        this.hostActivity = gameResources.hostActivity;
        GameLifeCycleCalllbackManager.getInstance().registerGameEntity(this);
    }


    /**
     * NOOP all the GameCallbacks so each entitiy doesn't have to implement every callback
     */
    @Override
    public void onCreateResources() { }

    @Override
    public void onCreateScene() { }
}
