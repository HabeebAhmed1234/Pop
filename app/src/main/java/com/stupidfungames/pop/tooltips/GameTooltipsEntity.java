package com.stupidfungames.pop.tooltips;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

public class GameTooltipsEntity extends BaseEntity {

    public GameTooltipsEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(TooltipTexts.class, new TooltipTexts());
        binder.bind(TooltipPreferences.class, new TooltipPreferences(this));
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
    }

    public void maybeShowTooltip(TooltipId id) {
        TooltipPreferences tooltipPreferences = get(TooltipPreferences.class);
        if (tooltipPreferences.shouldShowTooltip(id)) {
            tooltipPreferences.tooltipShown(id);
            showTooltip(id);
        }
    }

    public void maybeShowTooltip(TooltipId id, float anchorX, float anchorY) {
        TooltipPreferences tooltipPreferences = get(TooltipPreferences.class);
        if (tooltipPreferences.shouldShowTooltip(id)) {
            tooltipPreferences.tooltipShown(id);
            showAnchoredTooltip(id, anchorX, anchorY);
        }
    }

    private void showTooltip(final TooltipId id) {
        startActivity(GameTooltipActivity.forUnAnchoredTooltip(
                get(TooltipTexts.class).getTooltipText(id),
                get(Context.class)));

    }

    private void showAnchoredTooltip(TooltipId id, float anchorX, float anchorY) {
        startActivity(GameTooltipActivity.forAnchoredTooltip(
                                get(TooltipTexts.class).getTooltipText(id),
                                (int) anchorX,
                                (int) anchorY,
                get(Context.class)));
    }

    private void startActivity(Intent intent) {
        get(Context.class).startActivity(intent);
    }
}
