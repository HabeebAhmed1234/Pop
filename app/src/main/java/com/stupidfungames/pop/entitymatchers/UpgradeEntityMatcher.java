package com.stupidfungames.pop.entitymatchers;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.fixturedefdata.UpgradeUserData;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;

public class UpgradeEntityMatcher implements IEntityMatcher {

  @Override
  public boolean matches(IEntity pEntity) {
    @Nullable Object userdata = pEntity.getUserData();
    return userdata != null && userdata instanceof UpgradeUserData;
  }
}
