package com.stupidfungames.pop.nuke;

import androidx.annotation.Nullable;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.icons.BaseUpgradeableIconEntity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.tooltips.TooltipId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.GameConstants.NUKE_UNLOCK_THRESHOLD;
import static com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId.NUKE_ICON;

public class NukeIconEntity extends BaseUpgradeableIconEntity implements BaseStateMachine.Listener<NukeStateMachine.State> {

    private static final int NUM_UPGRADES = 3;

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

    public NukeIconEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {}

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
    protected int getIconUpgradesQuantity() {
        return NUM_UPGRADES;
    }

    @Override
    protected void onIconUnlocked() {
        super.onIconUnlocked();
        get(NukeStateMachine.class).transitionState(NukeStateMachine.State.READY);
    }

    @Override
    protected void onUpgraded(int upgradeLevel) {

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
