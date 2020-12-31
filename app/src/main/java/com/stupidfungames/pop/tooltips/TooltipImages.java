package com.stupidfungames.pop.tooltips;

import java.util.HashMap;
import java.util.Map;

public class TooltipImages {

  private static final Map<TooltipId, String> images = new HashMap<>();

  {
    images.put(TooltipId.BOMB_BUBBLE_TOOLTIP, "bomb_bubble.png");
    images.put(TooltipId.UPGRADE_TOOLTIP, "upgrade.png");
  }

  public String getTooltipImageAssetFilePath(TooltipId id) {
    if (!images.containsKey(id)) {
      return null;
    }
    return images.get(id);
  }
}
