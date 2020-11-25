package com.stupidfungames.pop.turrets.turret;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.TurretBulletUserData;
import com.stupidfungames.pop.pool.BaseSpriteItemInitializer;
import com.stupidfungames.pop.pool.ItemPool;
import com.stupidfungames.pop.pool.SpriteInitializerParams;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * A pool of items.
 */
public class TurretBulletSpritePool extends ItemPool {

  private final ItemInitializer initializer = new BaseSpriteItemInitializer<SpriteInitializerParams>() {

    @Override
    public Sprite createNew(SpriteInitializerParams params) {
      Sprite sprite = new Sprite(
          params.x,
          params.y,
          get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
          vertexBufferObjectManager);
      sprite.setColor(AndengineColor.RED);
      sprite.setUserData(new TurretBulletUserData());
      return sprite;
    }

    @Override
    public void onRecycle(Sprite item) {
      super.onRecycle(item);
      removePhysics(item);
    }
  };

  public TurretBulletSpritePool(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected ItemInitializer getInitializer() {
    return initializer;
  }

}
