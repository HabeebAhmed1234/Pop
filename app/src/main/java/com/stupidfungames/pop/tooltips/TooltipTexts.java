package com.stupidfungames.pop.tooltips;

import java.util.HashMap;
import java.util.Map;

class TooltipTexts {

  private static final Map<TooltipId, String> texts = new HashMap<>();

  {
    texts.put(TooltipId.TURRET_ICON_TOOLTIP,
        "Drag and drop turrets from this icon onto the play field.");
    texts.put(TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP,
        "Press and hold this icon to get a mace to pop bubbles with.");
    texts.put(TooltipId.WALLS_ICON_TOOLTIP,
        "Toggle this icon to start placing walls onto the screen. To place a wall press and drag on the screen.");
    texts.put(TooltipId.NUKE_ICON_TOOLTIP,
        "Tap this icon to pop all bubbles on the screen in waves");
    texts.put(TooltipId.MULTI_TOUCH_ICON_TOOLTIP,
        "Touch bubbles to pop them and all surrounding bubbles of the same colour.");
    texts.put(TooltipId.TOUCH_POP_TOOLTIP,
        "Touch the bubbles to pop them before they reach the bottom of the screen!\n\n Touch multiple bubbles of the same color to start a streak and earn more points!");
    texts.put(TooltipId.UPGRADE_TOOLTIP,
        "Tap on these bubbles to acquire an upgrade. You can then tap on a tool on the right to upgrade it. Every tool has a max of 3 upgrades. Tools will change color to cyan when they are upgradable");
    texts.put(TooltipId.BOMB_BUBBLE_TOOLTIP,
        "The below is a bomb bubble.\n\nTap on it while it is green to diffuse it and pop some bubbles in a radius around it.\n\nCareful! If it\'s countdown reaches 0 or you tap on it while it is red it will explode and end the game!\n\n The bomb will flash yellow before becoming red.");
  }

  public String getTooltipText(TooltipId id) {
    if (!texts.containsKey(id)) {
      throw new IllegalArgumentException("No text for tooltip id = " + id);
    }
    return texts.get(id);
  }
}
