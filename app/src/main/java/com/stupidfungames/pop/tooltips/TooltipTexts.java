package com.stupidfungames.pop.tooltips;

import androidx.annotation.StringRes;
import com.stupidfungames.pop.R;
import java.util.HashMap;
import java.util.Map;

class TooltipTexts {

  private static final Map<TooltipId, Integer> texts = new HashMap<>();

  {
    texts.put(TooltipId.TURRET_ICON_TOOLTIP, R.string.turret_tooltip);
    texts.put(TooltipId.SWORD_ICON_TOOLTIP, R.string.sword_tooltip);
    texts.put(TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP, R.string.ball_and_chain_tooltip);
    texts.put(TooltipId.WALLS_ICON_TOOLTIP, R.string.wall_tooltip);
    texts.put(TooltipId.NUKE_ICON_TOOLTIP, R.string.nuke_tooltip);
    texts.put(TooltipId.MULTI_TOUCH_ICON_TOOLTIP, R.string.multi_touch_icon_tooltip);
    texts.put(TooltipId.TOUCH_POP_TOOLTIP, R.string.touch_pop_tooltip);
    texts.put(TooltipId.UPGRADE_TOOLTIP, R.string.upgrade_tooltip);
    texts.put(TooltipId.BOMB_BUBBLE_TOOLTIP, R.string.bomb_bubble_tooltip);
    texts.put(TooltipId.NEGATIVE_SCORE_WARNING, R.string.negative_score_warning_tooltip);
  }

  @StringRes
  public int getTooltipText(TooltipId id) {
    if (!texts.containsKey(id)) {
      throw new IllegalArgumentException("No text for tooltip id = " + id);
    }
    return texts.get(id);
  }
}
