package com.wack.pop2;

import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsWorld;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
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

    protected final Scene scene;
    protected final PhysicsWorld physicsWorld;
    protected final VertexBufferObjectManager vertexBufferObjectManager;
    protected final Engine engine;
    protected final HostActivityInterface hostActivity;
    protected final int levelWidth;
    protected final int levelHeight;

    public BaseEntity(GameResources gameResources) {
        this.scene = gameResources.scene;
        this.physicsWorld = gameResources.physicsWorld;
        this.vertexBufferObjectManager = gameResources.vertexBufferObjectManager;
        this.engine = gameResources.engine;
        this.hostActivity = gameResources.hostActivity;
        this.levelWidth = ScreenUtils.getSreenSize().width;
        this.levelHeight = ScreenUtils.getSreenSize().height;
        GameLifeCycleCalllbackManager.getInstance().registerGameEntity(this);
    }

    /**
     * NOOP all the GameCallbacks so each entitiy doesn't have to implement every callback
     */
    @Override
    public void onCreateResources() {}

    @Override
    public void onCreateScene() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onLazyInit() {}

    protected void addToSceneWithTouch(Sprite sprite) {
        scene.registerTouchArea(sprite);
        addToScene(sprite);
    }

    protected void addToScene(IEntity entity) {
        scene.attachChild(entity);
    }

    protected void addToScene(IAreaShape entity, Body body) {
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(entity, body, true, true));
        addToScene(entity);
    }

    protected void removeFromScene(Body body) {
        PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByBody(body);
        if (physicsConnector != null) {
            physicsWorld.unregisterPhysicsConnector(physicsConnector);
            removeFromSceneInternal(physicsConnector.getShape());
        }
        physicsWorld.destroyBody(body);
    }

    protected void removeFromScene(IShape sprite) {
        PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sprite);
        if (physicsConnector != null) {
            physicsWorld.unregisterPhysicsConnector(physicsConnector);
            physicsWorld.destroyBody(physicsConnector.getBody());
        }
        removeFromSceneInternal(sprite);
    }

    protected boolean isInScene(final IEntity entity) {
        if (entity == null) return false;
        return scene.queryFirst(new IEntityMatcher() {
            @Override
            public boolean matches(IEntity pEntity) {
                return pEntity.equals(entity);
            }
        }) != null;
    }

    private void removeFromSceneInternal(IEntity entity) {
        if (entity instanceof ITouchArea) {
            scene.unregisterTouchArea((ITouchArea) entity);
        }
        scene.detachChild(entity);
    }
}
