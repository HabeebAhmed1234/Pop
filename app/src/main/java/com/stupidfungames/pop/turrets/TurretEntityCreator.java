package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimens;
import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimensCenterPos;
import static com.stupidfungames.pop.utils.ScreenUtils.dpToPx;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.turrets.turret.TurretEntity;
import java.util.List;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretEntityCreator extends BaseEntity {

  private static final int TURRET_BODY_SIZE_DP = 88;

  private static final int TURRET_CANNON_HEIGHT_PX = dpToPx(12);
  private static final int TURRET_CANNON_LENGTH_PX = dpToPx(32);

  public TurretEntityCreator(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    if (saveGame.turretPostitions != null) {
      for (List<Float> position : saveGame.turretPostitions) {
        TurretEntity turretEntity = createTurret(position.get(0), position.get(1));
        turretEntity.forceDrop();
      }
    }
  }

  public TurretEntity createTurret(float centerX, float centerY) {
    GameTexturesManager texturesManager = get(GameTexturesManager.class);
    ITextureRegion turretBodyTexture = texturesManager.getTextureRegion(TextureId.BALL);
    ITextureRegion turretCannonTexture = texturesManager.getTextureRegion(TextureId.LINE);

    Sprite turretBodySprite = new Sprite(
        0,
        0,
        turretBodyTexture,
        vertexBufferObjectManager);

    initSpriteDimensCenterPos(get(Context.class), turretBodySprite, centerX, centerY,
        TURRET_BODY_SIZE_DP);

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
