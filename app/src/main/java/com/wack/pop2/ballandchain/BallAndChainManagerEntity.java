package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;

/**
 * Manages the ball and chain tool that
 * gcan be used to pop bubbles by the user swinging around a
 * spike ball on a chain.
 */
public class BallAndChainManagerEntity extends BaseEntity {

    private BallAndChainCreatorEntity ballAndChainCreatorEntity;
    private BallAndChainCollisionManagerEntity ballAndChainCollisionManagerEntity;
    private BallAndChainHandleEntity ballAndChainHandleEntity;
    private BallAndChainIconEntity ballAndChainIconEntity;
    private BallAndChainDurabilityEntity ballAndChainDurabilityEntity;
    private BallAndChainColorEntity ballAndChainColorEntity;

    private BallAndChainStateMachine ballAndChainStateMachine = new BallAndChainStateMachine();

    private BallAndChain ballAndChain;

    public BallAndChainManagerEntity(
            GameTexturesManager texturesManager,
            GameSceneTouchListenerEntity gameSceneTouchListenerEntity,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            BubblePopperEntity bubblePopperEntity,
            GameResources gameResources) {
        super(gameResources);
        this.ballAndChainCreatorEntity = new BallAndChainCreatorEntity(texturesManager, gameResources);
        this.ballAndChainCollisionManagerEntity = new BallAndChainCollisionManagerEntity(ballAndChainStateMachine, bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
        this.ballAndChainHandleEntity = new BallAndChainHandleEntity(ballAndChainStateMachine, gameSceneTouchListenerEntity, gameResources);
        this.ballAndChainIconEntity = new BallAndChainIconEntity(ballAndChainStateMachine, gameIconsTrayEntity, texturesManager, gameResources);
        this.ballAndChainDurabilityEntity = new BallAndChainDurabilityEntity(ballAndChainStateMachine, gameResources);
        this.ballAndChainColorEntity = new BallAndChainColorEntity(ballAndChainStateMachine, gameResources);
    }

    @Override
    public void onCreateScene() {
        ballAndChain = ballAndChainCreatorEntity.createBallAndChain();
        ballAndChainColorEntity.setBallAndChain(ballAndChain);
        ballAndChainHandleEntity.setHandleJoint(ballAndChain.handle);
    }
}
