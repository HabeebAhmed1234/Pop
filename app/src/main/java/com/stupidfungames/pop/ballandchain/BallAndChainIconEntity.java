package com.stupidfungames.pop.ballandchain;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.icons.IconBaseEntity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.GameConstants.BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD;

/**
 * Appears when the ball and chain tool is unlocked. The user can tap and hold the icon to start
 * using the ball and chain.
 */
class BallAndChainIconEntity extends IconBaseEntity implements BallAndChainStateMachine.Listener<BallAndChainStateMachine.State> {

    private static final int NUM_UPGRADES = 3;

    private final IOnAreaTouchListener touchListener = new IOnAreaTouchListener() {
        @Override
        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            BallAndChainStateMachine stateMachine = get(BallAndChainStateMachine.class);
            if (pSceneTouchEvent.isActionDown() && stateMachine.getCurrentState() == BallAndChainStateMachine.State.UNLOCKED_CHARGED) {
                stateMachine.transitionState(BallAndChainStateMachine.State.IN_USE_CHARGED);
                return true;
            }
            return false;
        }
    };

    public BallAndChainIconEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        get(BallAndChainStateMachine.class).addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        get(BallAndChainStateMachine.class).removeAllStateTransitionListener(this);
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.BALL_AND_CHAIN_ICON;
    }

    @Override
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return GameIconsHostTrayEntity.IconId.BALL_AND_CHAIN_ICON;
    }

    @Override
    protected float getGameProgressPercentageUnlockThreshold() {
        return BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected int getIconUpgradesQuantity() {
        return NUM_UPGRADES;
    }

    @Override
    protected void onIconUnlocked() {
        get(BallAndChainStateMachine.class).transitionState(BallAndChainStateMachine.State.UNLOCKED_CHARGED);
    }

    @Override
    protected AndengineColor getUnlockedIconColor() {
        return AndengineColor.GREEN;
    }

    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }

    @Override
    protected TooltipId getIconTooltipId() {
        return TooltipId.BALL_AND_CHAIN_ICON_TOOLTIP;
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case UNLOCKED_CHARGED:
            case IN_USE_CHARGED:
                color = AndengineColor.GREEN;
                break;
            case UNLOCKED_DISCHARGED:
            case IN_USE_DISCHARGED:
                color = AndengineColor.RED;
                break;
        }
        setIconColor(color);
    }
}
