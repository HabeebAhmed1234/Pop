package com.wack.pop2.tooltips;

import com.wack.pop2.gamesettings.GamePreferences;

import java.util.HashMap;
import java.util.Map;

class TooltipPreferences {

    private final GamePreferences preferencesEntity;
    private Map<TooltipId, Boolean> cache = new HashMap<>();

    public TooltipPreferences(GamePreferences preferencesEntity) {
        this.preferencesEntity = preferencesEntity;
    }

    public void clearDebug() {
        for (TooltipId tooltipId : TooltipId.values()) {
            preferencesEntity.set(tooltipId.toString(), false);
        }
    }

    public boolean shouldShowTooltip(TooltipId id) {
        if (cache.containsKey(id)) {
            return !cache.get(id);
        }
        cache.put(id, preferencesEntity.getBoolean(id.toString()));
        return !cache.get(id);
    }

    public void tooltipShown(TooltipId id) {
        cache.put(id, true);
        preferencesEntity.set(id.toString(), true);
    }
}
