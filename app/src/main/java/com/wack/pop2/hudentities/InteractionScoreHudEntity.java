package com.wack.pop2.hudentities;

import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.InteractionScoreEventPayload;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.util.color.AndengineColor;

public class InteractionScoreHudEntity extends BaseHudTextEntity implements EventBus.Subscriber {

    public InteractionScoreHudEntity(GameFontsManager fontsManager, GameResources gameResources) {
        super(fontsManager, gameResources);
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
        return new int[] {50, ScreenUtils.getSreenSize().height - 250};
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
