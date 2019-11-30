package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Appears when the ball and chain tool is unlocked. The user can tap and hold the icon to start
 * using the ball and chain.
 */
public class BallAndChainIconEntity extends BaseEntity implements EventBus.Subscriber, BallAndChainStateMachine.Listener {

    private GameTexturesManager gameTexturesManager;

    private BallAndChainStateMachine stateMachine;
    private Sprite ballAndChainIconSprite;

    public BallAndChainIconEntity(
            BallAndChainStateMachine stateMachine,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
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
        addToScene(ballAndChainIconSprite);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        stateMachine
                .addTransitionListener(BallAndChainStateMachine.State.UNLOCKED_CHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.UNLOCKED_DISCHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.IN_USE_CHARGED, this)
                .addTransitionListener(BallAndChainStateMachine.State.IN_USE_DISCHARGED, this);
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
        if (newDifficulty >= BallAndChainConstants.BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD) {
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
    }
}
