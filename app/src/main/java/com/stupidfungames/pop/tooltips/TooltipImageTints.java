package com.stupidfungames.pop.tooltips;

import androidx.annotation.ColorRes;
import com.stupidfungames.pop.R;
import java.util.HashMap;
import java.util.Map;

public class TooltipImageTints {

  private static final Map<TooltipId, Integer> tints = new HashMap<>();

  {
    tints.put(TooltipId.BOMB_BUBBLE_TOOLTIP, R.color.green);
    tints.put(TooltipId.UPGRADE_TOOLTIP, R.color.pink);
    tints.put(TooltipId.SWORD_ICON_TOOLTIP, R.color.green);
    tints.put(TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP, R.color.green);
    tints.put(TooltipId.TURRET_ICON_TOOLTIP, R.color.green);
    tints.put(TooltipId.WALLS_ICON_TOOLTIP, R.color.green);
    tints.put(TooltipId.NUKE_ICON_TOOLTIP, R.color.green);
    tints.put(TooltipId.MULTI_TOUCH_ICON_TOOLTIP, R.color.green);
  }

  @ColorRes public int getTooltipImageTint(TooltipId id) {
    if (!tints.containsKey(id)) {
      return -1;
    }
    return tints.get(id);
  }

}
