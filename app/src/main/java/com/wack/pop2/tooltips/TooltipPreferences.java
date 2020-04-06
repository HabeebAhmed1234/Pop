package com.wack.pop2.tooltips;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.gamesettings.GamePreferencesManager;

import java.util.HashMap;
import java.util.Map;

class TooltipPreferences extends BaseEntity {

    private Map<TooltipId, Boolean> cache = new HashMap<>();

    public TooltipPreferences(BinderEnity parent) {
        super(parent);
    }

    public void clearDebug() {
        for (TooltipId tooltipId : TooltipId.values()) {
            cache.clear();
            GamePreferencesManager.set(get(Context.class), tooltipId.toString(), false);
        }
    }

    public boolean shouldShowTooltip(TooltipId id) {
        if (!cache.containsKey(id)) {
            cache.put(id, GamePreferencesManager.getBoolean(get(Context.class), id.toString()));
        }
        return !cache.get(id);
    }

    public void tooltipShown(TooltipId id) {
        cache.put(id, true);
        GamePreferencesManager.set(get(Context.class), id.toString(), true);
    }
}
