package com.wack.pop2.tooltips;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.gamesettings.GamePreferencesEntity;

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
            get(GamePreferencesEntity.class).set(tooltipId.toString(), false);
        }
    }

    public boolean shouldShowTooltip(TooltipId id) {
        if (!cache.containsKey(id)) {
            cache.put(id, get(GamePreferencesEntity.class).getBoolean(id.toString()));
        }
        return !cache.get(id);
    }

    public void tooltipShown(TooltipId id) {
        cache.put(id, true);
        get(GamePreferencesEntity.class).set(id.toString(), true);
    }
}
