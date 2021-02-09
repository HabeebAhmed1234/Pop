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
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class WallDraggableEntityCreator extends BaseDraggableEntityCreator {

  public WallDraggableEntityCreator(BinderEnity parent) {
    super(parent);
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
        .createBoxBody(physicsWorld, wallSprite, BodyType.STATIC, wallFixtureDef);

    addToScene(wallSprite);
    linkReversePhysics(wallSprite, wallBody);

    return new WallDraggableEntity(wallSprite, this);
  }
}
