package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.bubbletimeout.BubbleLifeCycleStateMachine.State;
import java.util.HashSet;
import java.util.Set;
import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;

/**
 * Stores all of the {@link BubbleLifecycleController}s needed to update the life of a bubble. Gets
 * set within the userdata of a bubble sprite.
 */
class BubbleLifecycleControllersManager extends BaseEntity {

  private final Set<BubbleLifecycleController> lifecycleControllers = new HashSet<>();
  private final BubbleLifeCycleStateMachine stateMachine;

  public BubbleLifecycleControllersManager(Engine engine,
      Sprite bubbleSprite, BaseEntity parent) {
    super(parent);
    stateMachine = new BubbleLifeCycleStateMachine();
    lifecycleControllers
        .add(new BubbleAboutToExplodeParticleSystemLifecycleController(bubbleSprite, stateMachine,
            this));
    lifecycleControllers
        .add(new BubbleLifecycleTransitionDriver(bubbleSprite, engine, stateMachine));
    lifecycleControllers.add(new BubbleLifecycleGameOverEntity(bubbleSprite, stateMachine, this));
  }

  /**
   * called when a bubble is re-spawned
   */
  public void reset() {
    if (stateMachine != null) {
      stateMachine.reset();
      stateMachine.transitionState(State.STABLE);
    }
  }

  public void onLifeycleControllersDestroy() {
    for (BubbleLifecycleController controller : lifecycleControllers) {
      controller.onLifeycleControllersDestroy();
    }
  }
}
