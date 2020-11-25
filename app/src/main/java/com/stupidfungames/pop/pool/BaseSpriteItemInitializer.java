package com.stupidfungames.pop.pool;

import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.pool.ItemPool.ItemInitializer;
import org.andengine.entity.sprite.Sprite;

public class BaseSpriteItemInitializer<P extends SpriteInitializerParams> implements
    ItemInitializer<Sprite, P> {

  @Override
  public Sprite createNew(P params) {
    return null;
  }

  @Override
  public void update(Sprite item, P params) {
    item.setX(params.x);
    item.setY(params.y);
    item.setVisible(true);
    item.setTouchEnabled(true);
  }

  @Override
  public void onRecycle(Sprite item) {
    item.setVisible(false);
    item.setTouchEnabled(false);
    item.clearOnDetachedListeners();
  }

  @Override
  public void destroy(Sprite item) {
    BaseEntityUserData userData = (BaseEntityUserData) item.getUserData();
    if (userData != null) {
      userData.reset();
    }
  }
}
