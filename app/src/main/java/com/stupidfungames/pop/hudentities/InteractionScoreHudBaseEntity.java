package com.stupidfungames.pop.hudentities;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.InteractionScoreEventPayload;
import com.stupidfungames.pop.utils.ScreenUtils;

import org.andengine.util.color.AndengineColor;

public class InteractionScoreHudBaseEntity extends HudTextBaseEntity implements EventBus.Subscriber {

    public InteractionScoreHudBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {

    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get().subscribe(GameEvent.INTERACTION_SCORE_CHANGED, this);
    }

    @Override
    String getInitialText() {
        return "IntScore: --------";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.INTERACTION_SCORE_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.INTERACTION_SCORE_CHANGED) {
            InteractionScoreEventPayload interactionScoreEventPayload = (InteractionScoreEventPayload) payload;
            updateText("IntScore: " + interactionScoreEventPayload.interactionScore);
        }
    }

    @Override
    int[] getTextPosition() {
        return new int[] {50, ScreenUtils.getSreenSize().heightPx - 250};
    }

    @Override
    int getMaxStringLength() {
        return 30;
    }

    @Override
    AndengineColor getTextColor() {
        return AndengineColor.WHITE;
    }
}
