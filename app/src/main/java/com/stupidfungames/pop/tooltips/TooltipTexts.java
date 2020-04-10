package com.stupidfungames.pop.tooltips;

import java.util.HashMap;
import java.util.Map;

class TooltipTexts {

    private static final Map<TooltipId, String> texts = new HashMap<>();

    {
        texts.put(TooltipId.TURRET_ICON_TOOLTIP, "Drag and drop turrets from this icon onto the play field");
        texts.put(TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP, "Press and hold this icon to get a mace to pop bubbles with");
        texts.put(TooltipId.WALLS_ICON_TOOLTIP, "Toggle this icon to start placing walls onto the screen. To place a wall press and drag on the screen");
        texts.put(TooltipId.NUKE_ICON_TOOLTIP, "Tap this icon to pop all bubbles on the screen in 3 waves");
        texts.put(TooltipId.TOUCH_POP_TOOLTIP, "Touch the bubbles to pop them before they reach the bottom of the screen!");
    }

    public String getTooltipText(TooltipId id) {
        if (!texts.containsKey(id)) {
            throw new IllegalArgumentException("No text for tooltip id = " + id);
        }
        return texts.get(id);
    }
}
