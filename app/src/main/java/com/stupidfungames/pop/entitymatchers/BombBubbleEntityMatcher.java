package com.stupidfungames.pop.entitymatchers;

import com.stupidfungames.pop.fixturedefdata.BombBubbleEntityUserData;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

public class BombBubbleEntityMatcher implements IEntityMatcher {

  @Override
  public boolean matches(IEntity pEntity) {
    return pEntity.getUserData() != null && pEntity
        .getUserData() instanceof BombBubbleEntityUserData;
  }
}
