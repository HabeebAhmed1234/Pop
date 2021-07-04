package com.stupidfungames.pop.tooltips;

import java.util.HashMap;
import java.util.Map;

public class TooltipImages {

  private static final Map<TooltipId, String> images = new HashMap<>();

  {
    images.put(TooltipId.BOMB_BUBBLE_TOOLTIP, "bomb_bubble.png");
    images.put(TooltipId.UPGRADE_TOOLTIP, "upgrade.png");
    images.put(TooltipId.SWORD_ICON_TOOLTIP, "sword_icon.png");
    images.put(TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP, "ball_and_chain_icon.png");
    images.put(TooltipId.TURRET_ICON_TOOLTIP, "turrets_icon.png");
    images.put(TooltipId.WALLS_ICON_TOOLTIP, "walls_icon.png");
    images.put(TooltipId.NUKE_ICON_TOOLTIP, "nuke_icon.png");
    images.put(TooltipId.MULTI_TOUCH_ICON_TOOLTIP, "multi_pop_icon.png");
  }

  public String getTooltipImageAssetFilePath(TooltipId id) {
    if (!images.containsKey(id)) {
      return null;
    }
    return images.get(id);
  }
}
