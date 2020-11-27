package com.stupidfungames.pop;

import android.util.Log;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.physics.PhysicsConnector;
import com.stupidfungames.pop.physics.PhysicsWorld;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnAreaTouchListener;
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
 * will ping methods within game entities which will then update the underlying items.
 *
 * All data is stored within these entity objects that extends this BaseEntity
 */
public abstract class BaseEntity extends BinderEnity implements
    GameLifeCycleCalllbackManager.GameCallback {

  protected final Scene scene;
  protected final PhysicsWorld physicsWorld;
  protected final VertexBufferObjectManager vertexBufferObjectManager;
  protected final Engine engine;
  protected final HostActivity hostActivity;
  protected final int levelWidth;
  protected final int levelHeight;

  public BaseEntity(BinderEnity parent) {
    super(parent);
    GameResources gameResources = get(GameResources.class);
    this.scene = gameResources.scene;
    this.physicsWorld = gameResources.physicsWorld;
    this.vertexBufferObjectManager = gameResources.vertexBufferObjectManager;
    this.engine = gameResources.engine;
    this.hostActivity = gameResources.hostActivity;
    this.levelWidth = ScreenUtils.getSreenSize().widthPx;
    this.levelHeight = ScreenUtils.getSreenSize().heightPx;
    get(GameLifeCycleCalllbackManager.class).registerGameEntity(this);
  }

  /**
   * NOOP all the GameCallbacks so each entitiy doesn't have to implement every callback
   */
  @Override
  public void onCreateResources() {
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
  }

  @Override
  public void onCreateScene() {
  }

  @Override
  public void onDestroy() {
    get(GameLifeCycleCalllbackManager.class).unRegisterGameEntity(this);
    destroyBindings();
  }

  protected void addToSceneWithTouch(IEntity parentEntity, IAreaShape childSprite,
      IOnAreaTouchListener areaTouchListener) {
    setUpTouch(childSprite, areaTouchListener);
    parentEntity.attachChild(childSprite);
  }

  protected void addToSceneWithTouch(IAreaShape sprite, IOnAreaTouchListener areaTouchListener) {
    setUpTouch(sprite, areaTouchListener);
    addToScene(sprite);
  }

  protected void addToSceneWithTouch(IEntity parent, Sprite sprite,
      IOnAreaTouchListener areaTouchListener) {
    setUpTouch(sprite, areaTouchListener);
    addToScene(parent, sprite);
  }

  protected void addToSceneWithTouch(IAreaShape entity, Body body,
      IOnAreaTouchListener areaTouchListener) {
    setUpTouch(entity, areaTouchListener);
    addToScene(entity, body);
  }

  protected void addToScene(IEntity parent, IEntity sprite) {
    parent.attachChild(sprite);
  }

  protected void addToScene(IEntity entity) {
    if (!entity.isAttached()) {
      scene.attachChild(entity);
    }
  }

  protected void addToScene(IAreaShape entity, Body body) {
    linkPhysics(entity, body);
    addToScene(entity);
  }

  protected void linkPhysics(final IAreaShape entity, final Body body) {
    physicsWorld.registerPhysicsConnector(new PhysicsConnector(entity, body, true, true));
  }

  protected void removeFromScene(final Body body) {
    removeFromScene(body, true);
  }

  protected void removeFromScene(final Body body, final boolean alsoRemoveSprite) {
    PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager()
        .findPhysicsConnectorByBody(body);
    if (physicsConnector != null) {
      physicsWorld.unregisterPhysicsConnector(physicsConnector);
      if (alsoRemoveSprite) {
        removeFromSceneInternal(physicsConnector.getShape());
      }
      physicsConnector.clear();
    }
    physicsWorld.destroyBody(body);
  }

  protected void removePhysics(Sprite sprite) {
    PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager()
        .findPhysicsConnectorByShape(sprite);
    physicsWorld.unregisterPhysicsConnector(physicsConnector);
    physicsWorld.destroyBody(physicsConnector.getBody());
    physicsConnector.clear();
  }

  protected void removeFromScene(final IShape sprite) {
    PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager()
        .findPhysicsConnectorByShape(sprite);
    if (physicsConnector != null) {
      physicsWorld.unregisterPhysicsConnector(physicsConnector);
      physicsWorld.destroyBody(physicsConnector.getBody());
      physicsConnector.clear();
    }
    removeFromSceneInternal(sprite);
  }

  protected boolean isInScene(final IEntity entity) {
    return entity != null && entity.isAttached();
  }

  private void removeFromSceneInternal(IEntity entity) {
    if (entity instanceof ITouchArea) {
      scene.unregisterTouchArea((ITouchArea) entity);
    }
    scene.detachChild(entity);
    Object userData = entity.getUserData();
    if (userData instanceof BaseEntityUserData) {
      ((BaseEntityUserData) userData).reset();
    }
    entity.setUserData(null);
  }

  private void setUpTouch(IAreaShape shape, IOnAreaTouchListener areaTouchListener) {
    scene.registerTouchArea(shape);
    shape.setOnAreaTouchListener(areaTouchListener);
  }
}
