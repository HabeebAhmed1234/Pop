package com.stupidfungames.pop.hudentities;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.util.color.AndengineColor;

public class StreakHudEntity extends HudTextBaseEntity {

  public StreakHudEntity(BinderEnity parent) {
    super(parent, true);
  }

  @Override
  String getInitialText() {
    return "";
  }

  @Override
  int[] getTextPositionPx(int[] textDimensPx) {
    return new int[] {
        (int) get(ScoreHudEntity.class).getTextRightEdgePx(),
        ScreenUtils.getSreenSize().heightPx - textDimensPx[1]};
  }

  @Override
  int getMaxStringLength() {
    return 5;
  }

  @Override
  AndengineColor getInitialTextColor() {
    return AndengineColor.RED;
  }
}
