package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.sprite.Sprite;

/**
 * Icon used to utilized turrets. Once unlocked this icon appears. The user can then press down on
 * the icon and a turret appears under their finger. They can then drag the turret to where they
 * want it. The icon has a number on it to show the number of turrets in stock. The user can drag
 * turrets back onto the icon to store them this increasing the number turrets in stock on the icon.
 */
class TurretsIconEntity extends BaseEntity /*implements GameAreaTouchListenerEntity.AreaTouchListener, EventBus.Subscriber */{

    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;

    // True if the Turrets have been unlocked in the game
    private boolean isUnlocked;
    private Sprite turretIconSprite;

    public TurretsIconEntity(
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
    }

    /*@Override
    public void onCreateResources() {
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.BALL_AND_CHAIN_ICON);
        turretIconSprite = new Sprite(
                ScreenUtils.getSreenSize().width - textureRegion.getWidth() * 2,
                ScreenUtils.getSreenSize().height - textureRegion.getHeight() * 2,
                textureRegion,
                vertexBufferObjectManager);
        turretIconSprite.setUserData(new TurretsIconUserData());
        addToSceneWithTouch(turretIconSprite);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        touchListenerEntity.addAreaTouchListener(TurretsIconUserData.class, this);
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
    }*/
}