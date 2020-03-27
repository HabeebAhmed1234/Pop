package com.wack.pop2.tooltips;

import android.content.Intent;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.gamesettings.GamePreferences;

public class GameTooltipsEntity extends BaseEntity {

    private TooltipTexts tooltipTexts;
    private TooltipPreferences tooltipPreferences;

    public GameTooltipsEntity(
            GamePreferences gamePreferencesEntity,
            GameResources gameResources) {
        super(gameResources);
        this.tooltipTexts = new TooltipTexts();
        this.tooltipPreferences = new TooltipPreferences(gamePreferencesEntity);
    }

    public void maybeShowTooltip(TooltipId id) {
        if (tooltipPreferences.shouldShowTooltip(id)) {
            showTooltip(id);
        }
    }

    public void maybeShowTooltip(TooltipId id, float anchorX, float anchorY) {
        if (tooltipPreferences.shouldShowTooltip(id)) {
            showAnchoredTooltip(id, anchorX, anchorY);
        }
    }

    private void showTooltip(final TooltipId id) {
        startActivity(GameTooltipActivity.forUnAnchoredTooltip(
                tooltipTexts.getTooltipText(id),
                hostActivity.getActivityContext()));

    }

    private void showAnchoredTooltip(TooltipId id, float anchorX, float anchorY) {
        startActivity(GameTooltipActivity.forAnchoredTooltip(
                                tooltipTexts.getTooltipText(id),
                                (int) anchorX,
                                (int) anchorY,
                                hostActivity.getActivityContext()));
    }

    private void startActivity(Intent intent) {
        hostActivity.getActivityContext().startActivity(intent);
    }
}
