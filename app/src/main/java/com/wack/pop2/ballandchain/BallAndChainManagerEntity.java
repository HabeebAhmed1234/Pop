package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.jbox2d.dynamics.joints.MouseJoint;

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

    private BallAndChainStateMachine ballAndChainStateMachine = new BallAndChainStateMachine();

    private MouseJoint mouseJoint;

    public BallAndChainManagerEntity(
            GameTexturesManager texturesManager,
            GameAreaTouchListenerEntity gameAreaTouchListenerEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            BubblePopperEntity bubblePopperEntity,
            GameResources gameResources) {
        super(gameResources);
        this.ballAndChainCreatorEntity = new BallAndChainCreatorEntity(texturesManager, gameResources);
        this.ballAndChainCollisionManagerEntity = new BallAndChainCollisionManagerEntity(ballAndChainStateMachine, bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
        this.ballAndChainHandleEntity = new BallAndChainHandleEntity(ballAndChainStateMachine, gameResources);
        this.ballAndChainIconEntity = new BallAndChainIconEntity(ballAndChainStateMachine, gameAreaTouchListenerEntity, texturesManager, gameResources);
    }

    @Override
    public void onCreateScene() {
        mouseJoint = ballAndChainCreatorEntity.createBallAndChain();
        ballAndChainHandleEntity.setHandleJoint(mouseJoint);
    }
}
