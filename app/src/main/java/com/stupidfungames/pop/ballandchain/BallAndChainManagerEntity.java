package com.stupidfungames.pop.ballandchain;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

/**
 * Manages the ball and chain tool that gcan be used to pop bubbles by the user swinging around a
 * spike ball on a chain.
 */
public class BallAndChainManagerEntity extends BaseEntity {

  private BallAndChain ballAndChain;

  public BallAndChainManagerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(BallAndChainStateMachine.class, new BallAndChainStateMachine());
    binder.bind(BallAndChainCreatorEntity.class, new BallAndChainCreatorEntity(this));
    binder.bind(BallAndChainCollisionManagerEntity.class,
        new BallAndChainCollisionManagerEntity(this));
    binder.bind(BallAndChainHandleEntity.class, new BallAndChainHandleEntity(this));
    binder.bind(BallAndChainIconEntity.class, new BallAndChainIconEntity(this));
    binder.bind(BallAndChainDurabilityEntity.class, new BallAndChainDurabilityEntity(this));
    binder.bind(BallAndChainColorEntity.class, new BallAndChainColorEntity(this));
  }

  @Override
  public void onCreateScene() {
    ballAndChain = get(BallAndChainCreatorEntity.class).createBallAndChain();
    get(BallAndChainColorEntity.class).setBallAndChain(ballAndChain);
    get(BallAndChainHandleEntity.class).setHandleJoint(ballAndChain.handle);
  }
}
