package com.wack.pop2.bubbletimeout;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.OnDetachedListener;
import org.andengine.entity.shape.IShape;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all of the {@link BubbleLifecycleController}s needed to update the life
 * of a bubble. Gets set within the userdata of a bubble sprite.
 */
public class BubbleLifecycleControllersManager implements OnDetachedListener {

    private Set<BubbleLifecycleController> lifecycleControllers = new HashSet<>();

    public BubbleLifecycleControllersManager(Engine engine, IShape bubbleSprite) {
        bubbleSprite.setOnDetachedListener(this);
        initControllers(engine, bubbleSprite);
    }

    @Override
    public void onDetached(IEntity entity) {
        onDestroy();
        entity.removeOnDetachedListener();
    }

    /**
     * Called when the bubble is being destroyed
     */
    public void onDestroy() {
        for (BubbleLifecycleController controller : lifecycleControllers) {
            controller.onDestroy();
        }

    }

    private void initControllers(Engine engine, IShape bubbleSprite) {
        BubbleLifeCycleStateMachine stateMachine = new BubbleLifeCycleStateMachine();
        lifecycleControllers.add(new BubbleBlinkAnimationManager(bubbleSprite, stateMachine));
        lifecycleControllers.add(new BubbleLIfecycleTransitionDriver(engine, stateMachine));
    }
}
