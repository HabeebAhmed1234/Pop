package com.wack.pop2;

import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.tooltips.GameTooltipsEntity;
import com.wack.pop2.tooltips.TooltipId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

public class GameStartTooltipEntity extends BaseEntity {

    private static final float TOOLTIP_DELAY_SECONDS = 1;

    public GameStartTooltipEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        scene.registerUpdateHandler(new TimerHandler(TOOLTIP_DELAY_SECONDS, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                get(GameTooltipsEntity.class).maybeShowTooltip(TooltipId.TOUCH_POP_TOOLTIP);
            }
        }));
    }
}
