package com.wack.pop2.tooltips;

import com.wack.pop2.gamesettings.GamePreferences;

class TooltipPreferences {

    private final GamePreferences preferencesEntity;

    public TooltipPreferences(GamePreferences preferencesEntity) {
        this.preferencesEntity =preferencesEntity;
    }

    public boolean shouldShowTooltip(TooltipId id) {
        return true;
        // True if tooltip was shown
        // return !preferencesEntity.getBoolean(id.toString());
    }

    public void tooltipShown(TooltipId id) {
        preferencesEntity.set(id.toString(), true);
    }
}
