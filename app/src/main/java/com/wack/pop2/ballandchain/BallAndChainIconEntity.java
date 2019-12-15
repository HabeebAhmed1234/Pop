package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BallAndChainIconUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

/**
 * Appears when the ball and chain tool is unlocked. The user can tap and hold the icon to start
 * using the ball and chain.
 */
class BallAndChainIconEntity extends BaseEntity implements EventBus.Subscriber, BallAndChainStateMachine.Listener<BallAndChainStateMachine.State>, GameAreaTouchListenerEntity.AreaTouchListener {

    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;

    private BallAndChainStateMachine stateMachine;
    private Sprite ballAndChainIconSprite;

    public BallAndChainIconEntity(
            BallAndChainStateMachine stateMachine,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.stateMachine = stateMachine;
    }

    @Override
    public void onCreateResources() {
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.BALL_AND_CHAIN_ICON);
        ballAndChainIconSprite = new Sprite(
                ScreenUtils.getSreenSize().width - textureRegion.getWidth(),
                ScreenUtils.getSreenSize().height - textureRegion.getHeight(),
                textureRegion,
                vertexBufferObjectManager);
        ballAndChainIconSprite.setUserData(new BallAndChainIconUserData());
        addToSceneWithTouch(ballAndChainIconSprite);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        stateMachine.addAllStateTransitionListener(this);
        touchListenerEntity.addAreaTouchListener(BallAndChainIconUserData.class, this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onScoreChanged(difficultyChangedEventPayload.newDifficulty);
                break;
        }
    }

    private void onScoreChanged(int newDifficulty) {
        if (newDifficulty >= BallAndChainConstants.BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD
                && stateMachine.getCurrentState() == BallAndChainStateMachine.State.LOCKED) {
            stateMachine.transitionState(
                    BallAndChainStateMachine.State.UNLOCKED_CHARGED);
        }
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        switch (newState) {
            case UNLOCKED_CHARGED:
                break;
            case UNLOCKED_DISCHARGED:
                break;
            case IN_USE_CHARGED:
                break;
            case IN_USE_DISCHARGED:
                break;
        }
        setIconColor(newState);
    }

    /**
     * Given the new state of the ball and chain we set the right color for the icon
     * @param newState
     */
    private void setIconColor(BallAndChainStateMachine.State newState) {
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
        ballAndChainIconSprite.setColor(color);
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
