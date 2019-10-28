package com.wack.pop2;

import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsWorld;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.jbox2d.dynamics.Body;

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

    protected void addToScene(IEntity entity) {
        scene.attachChild(entity);
    }

    protected void removeFromSceneAndCleanupPhysics(Body body) {
        PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByBody(body);
        physicsWorld.unregisterPhysicsConnector(physicsConnector);
        physicsWorld.destroyBody(body);
        removeFromScene(physicsConnector.getShape());
    }

    protected void removeFromSceneAndCleanupPhysics(Sprite sprite) {
        PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sprite);
        physicsWorld.unregisterPhysicsConnector(physicsConnector);
        physicsWorld.destroyBody(physicsConnector.getBody());
        removeFromScene(physicsConnector.getShape());
    }

    protected void removeFromScene(IEntity entity) {
        if (entity instanceof ITouchArea) {
            scene.unregisterTouchArea((ITouchArea) entity);
        }
        scene.detachChild(entity);
    }
}
