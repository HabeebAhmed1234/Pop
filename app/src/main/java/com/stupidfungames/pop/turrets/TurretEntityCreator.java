package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimens;
import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimensCenterPos;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntityCreator;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.turrets.turret.TurretEntity;
import java.util.List;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretEntityCreator extends BaseDraggableEntityCreator {

  private static final float TURRET_BODY_SIZE_PX = 300;
  private static final float TURRET_CANNON_HEIGHT_PX = 36;
  private static final float TURRET_CANNON_LENGTH_PX = 96;

  public TurretEntityCreator(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    if (saveGame.turretPostitions != null) {
      for (List<Float> position : saveGame.turretPostitions) {
        TurretEntity turretEntity = createDraggableEntity(position.get(0), position.get(1));
        turretEntity.forceDrop();
      }
    }
  }

  @Override
  public TurretEntity createDraggableEntity(float centerX, float centerY) {
    GameTexturesManager texturesManager = get(GameTexturesManager.class);
    ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
    ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

    Sprite turretBodySprite = new Sprite(
        0,
        0,
        turretBodyTexture,
        vertexBufferObjectManager);

    initSpriteDimensCenterPos(turretBodySprite, centerX, centerY, TURRET_BODY_SIZE_PX);

    addToScene(turretBodySprite);

    final Sprite turretCannonSprite = new Sprite(
        0,
        0,
        turretCannonTexture,
        vertexBufferObjectManager);

    initSpriteDimens(
        turretCannonSprite,
        (int) turretBodySprite.getWidth() / 2,
        (int) turretBodySprite.getHeight() / 2 - TURRET_CANNON_HEIGHT_PX / 2,
        TURRET_CANNON_LENGTH_PX,
        TURRET_CANNON_HEIGHT_PX);

    turretBodySprite.attachChild(turretCannonSprite);
    turretCannonSprite.setRotationCenter(0f, TURRET_CANNON_HEIGHT_PX / 2);

    return new TurretEntity(turretBodySprite, turretCannonSprite, this);
  }
}
