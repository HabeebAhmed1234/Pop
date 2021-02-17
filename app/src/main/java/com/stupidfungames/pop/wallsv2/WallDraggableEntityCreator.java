package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimensCenterPos;

import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntityCreator;
import com.stupidfungames.pop.fixturedefdata.WallV2EntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.savegame.SaveGame;
import java.util.List;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class WallDraggableEntityCreator extends BaseDraggableEntityCreator {

  private static final float DRAGGABLE_WALL_SCALE_FACTOR_X = 0.7f;
  private static final float DRAGGABLE_WALL_SCALE_FACTOR_Y = 0.8f;

  public WallDraggableEntityCreator(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    if (saveGame.wallV2Postitions != null) {
      for (List<Float> position : saveGame.wallV2Postitions) {
        WallDraggableEntity wallDraggableEntity = createDraggableEntity(position.get(0),
            position.get(1));
        wallDraggableEntity.onForceDropped();
      }
    }
  }

  @Override
  public WallDraggableEntity createDraggableEntity(float centerX, float centerY) {
    GameTexturesManager texturesManager = get(GameTexturesManager.class);
    ITextureRegion wallTexture = texturesManager.getTextureRegion(TextureId.WALL_V2);

    WallV2EntityUserData wallUserData = new WallV2EntityUserData();
    Sprite wallSprite = new Sprite(
        0,
        0,
        wallTexture,
        vertexBufferObjectManager);
    wallSprite.setUserData(wallUserData);
    initSpriteDimensCenterPos(wallSprite, centerX, centerY);
    final FixtureDef wallFixtureDef = GameFixtureDefs.WALL_FIXTURE_DEF;
    wallFixtureDef.setFilter(CollisionFilters.WALL_FILTER);
    wallFixtureDef.setUserData(wallUserData);
    Body wallBody = PhysicsFactory
        .createBoxBody(
            physicsWorld,
            wallSprite,
            BodyType.STATIC,
            new float[]{DRAGGABLE_WALL_SCALE_FACTOR_X, DRAGGABLE_WALL_SCALE_FACTOR_Y},
            wallFixtureDef);

    // Create wall health bar
    Sprite wallHealthSprite = new Sprite(
        0,
        0,
        texturesManager.getTextureRegion(TextureId.WALL_V2_FILL),
        vertexBufferObjectManager);

    addToScene(wallSprite);
    addToScene(wallSprite, wallHealthSprite);
    linkReversePhysics(wallSprite, wallBody);

    return new WallDraggableEntity(wallSprite, this);
  }
}
