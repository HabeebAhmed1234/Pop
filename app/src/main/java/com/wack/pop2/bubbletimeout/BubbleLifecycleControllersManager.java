package com.wack.pop2.bubbletimeout;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all of the {@link BubbleLifecycleController}s needed to update the life
 * of a bubble. Gets set within the userdata of a bubble sprite.
 */
class BubbleLifecycleControllersManager {

    private Set<BubbleLifecycleController> lifecycleControllers = new HashSet<>();

    public BubbleLifecycleControllersManager(Engine engine, Sprite bubbleSprite) {
        initControllers(engine, bubbleSprite);
    }

    /**
     * Called when the bubble is being destroyed
     */
    public void onDestroy() {
        for (BubbleLifecycleController controller : lifecycleControllers) {
            controller.onDestroy();
        }

    }

    private void initControllers(Engine engine, Sprite bubbleSprite) {
        BubbleLifeCycleStateMachine stateMachine = new BubbleLifeCycleStateMachine();
        lifecycleControllers.add(new BubbleBlinkAnimationManager(bubbleSprite, stateMachine));
        lifecycleControllers.add(new BubbleLifecycleTransitionDriver(engine, stateMachine));
        lifecycleControllers.add(new BubbleLifecycleGameOverEntity(bubbleSprite, stateMachine));
    }
}
