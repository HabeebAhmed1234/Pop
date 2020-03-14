package com.wack.pop2.ballandchain;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BallAndChainIconUserData;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.icons.BaseIconEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.ballandchain.BallAndChainConstants.BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD;

/**
 * Appears when the ball and chain tool is unlocked. The user can tap and hold the icon to start
 * using the ball and chain.
 */
class BallAndChainIconEntity extends BaseIconEntity implements BallAndChainStateMachine.Listener<BallAndChainStateMachine.State> {

    private BallAndChainStateMachine stateMachine;

    public BallAndChainIconEntity(
            BallAndChainStateMachine stateMachine,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.stateMachine = stateMachine;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.BALL_AND_CHAIN_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return BallAndChainIconUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new BallAndChainIconUserData();
    }

    @Override
    protected GameIconsHostTrayEntity.IconId getIconId() {
        return GameIconsHostTrayEntity.IconId.BALL_AND_CHAIN_ICON;
    }

    @Override
    protected float getDifficultyIntervalUnlockThreshold() {
        return BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {
        stateMachine.transitionState(BallAndChainStateMachine.State.UNLOCKED_CHARGED);
    }

    @Override
    protected AndengineColor getUnlockedColor() {
        return AndengineColor.GREEN;
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

    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown() && stateMachine.getCurrentState() == BallAndChainStateMachine.State.UNLOCKED_CHARGED) {
            stateMachine.transitionState(BallAndChainStateMachine.State.IN_USE_CHARGED);
            return true;
        }
        return false;
    }
}
