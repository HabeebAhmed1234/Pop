package com.stupidfungames.pop.bubbletimeout;

import com.stupidfungames.pop.resources.sounds.GameSoundsManager;

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

    public BubbleLifecycleControllersManager(GameSoundsManager soundsManager, Engine engine, Sprite bubbleSprite) {
        initControllers(soundsManager, engine, bubbleSprite);
    }

    /**
     * Called when the bubble is being destroyed
     */
    public void onDestroy() {
        for (BubbleLifecycleController controller : lifecycleControllers) {
            controller.onDestroy();
        }

    }

    private void initControllers(GameSoundsManager soundsManager, Engine engine, Sprite bubbleSprite) {
        BubbleLifeCycleStateMachine stateMachine = new BubbleLifeCycleStateMachine();
        lifecycleControllers.add(new BubbleBlinkAnimationManager(bubbleSprite, stateMachine, soundsManager));
        lifecycleControllers.add(new BubbleLifecycleTransitionDriver(bubbleSprite, engine, stateMachine));
        lifecycleControllers.add(new BubbleLifecycleGameOverEntity(bubbleSprite, stateMachine));
    }
}
