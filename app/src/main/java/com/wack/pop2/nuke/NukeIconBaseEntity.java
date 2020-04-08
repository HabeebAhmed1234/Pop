package com.wack.pop2.nuke;

import androidx.annotation.Nullable;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.icons.IconBaseEntity;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.tooltips.TooltipId;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.GameConstants.NUKE_UNLOCK_THRESHOLD;
import static com.wack.pop2.gameiconstray.GameIconsHostTrayEntity.IconId.NUKE_ICON;

public class NukeIconBaseEntity extends IconBaseEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            if (get(NukeStateMachine.class).getCurrentState() == NukeStateMachine.State.READY) {
                get(NukerEntity.class).startNuke();
                return true;
            }
            return false;
        }
    };

    public NukeIconBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {

    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        get(NukeStateMachine.class).addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        get(NukeStateMachine.class).removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(NukeStateMachine.State newState) {
        AndengineColor color = AndengineColor.TRANSPARENT;

        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case READY:
                color = AndengineColor.GREEN;
                break;
            case NUKING:
                color = AndengineColor.YELLOW;
                break;
            case COOLDOWN:
                color = AndengineColor.RED;
                break;
        }

        setIconColor(color);
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.NUKE_ICON;
    }

    @Override
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return NUKE_ICON;
    }

    @Override
    protected float getGameProgressPercentageUnlockThreshold() {
        return NUKE_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {
        get(NukeStateMachine.class).transitionState(NukeStateMachine.State.READY);
    }

    @Override
    protected AndengineColor getUnlockedIconColor() {
        return AndengineColor.GREEN;
    }

    @Nullable
    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }

    @Override
    protected TooltipId getIconTooltipId() {
        return TooltipId.NUKE_ICON_TOOLTIP;
    }
}
