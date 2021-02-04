package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimens;
import static com.stupidfungames.pop.utils.GeometryUtils.initSpriteDimensCenterPos;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.turrets.turret.TurretEntity;
import java.util.List;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretEntityCreator extends BaseEntity {

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

    initSpriteDimensCenterPos(turretBodySprite, centerX, centerY,
        getDimenPx(R.dimen.turret_body_size));

    addToScene(turretBodySprite);

    final Sprite turretCannonSprite = new Sprite(
        0,
        0,
        turretCannonTexture,
        vertexBufferObjectManager);

    float turretCannonHeightPx = getDimenPx(R.dimen.turret_cannon_height);
    float turretCannonLengthPx = getDimenPx(R.dimen.turret_cannon_length);

    initSpriteDimens(
        turretCannonSprite,
        (int) turretBodySprite.getWidth() / 2,
        (int) turretBodySprite.getHeight() / 2 - turretCannonHeightPx / 2,
        turretCannonLengthPx,
        turretCannonHeightPx);

    turretBodySprite.attachChild(turretCannonSprite);
    turretCannonSprite.setRotationCenter(0f, turretCannonHeightPx / 2);

    return new TurretEntity(turretBodySprite, turretCannonSprite, this);
  }
}
