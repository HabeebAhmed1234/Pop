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
  }

  @ColorRes public int getTooltipImageTint(TooltipId id) {
    if (!tints.containsKey(id)) {
      return -1;
    }
    return tints.get(id);
  }

}
