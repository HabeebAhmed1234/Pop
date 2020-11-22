package com.stupidfungames.pop.turrets.turret;

import com.stupidfungames.pop.BaseSpritePool;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * A pool of sprites.
 */
public class TurretBulletSpritePool extends BaseSpritePool {

  public TurretBulletSpritePool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected Sprite createNewSprite(float x, float y) {
    Sprite sprite = new Sprite(
        x,
        y,
        get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
        vertexBufferObjectManager);
    sprite.setColor(AndengineColor.RED);
    sprite.setUserData(new TurretBulletUserData());
    return sprite;
  }
}
