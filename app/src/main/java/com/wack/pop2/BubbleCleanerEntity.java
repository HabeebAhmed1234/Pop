package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.entitymatchers.BubblesEntityMatcher;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.shape.IShape;

import java.util.List;

/**
 * Sometimes bubbles slip through the physics floor we have in the game so we can make this cleaner
 * to help catch those exceptions.
 */
public class BubbleCleanerEntity extends BaseEntity{

    private static final float BUBBLE_OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS = 1f;

    public BubbleCleanerEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        engine.registerUpdateHandler(
                new TimerHandler(
                        BUBBLE_OUT_OF_BOUNDS_CHECK_INTERVAL_SECONDS,
                        true,
                        new ITimerCallback() {
                            @Override
                            public void onTimePassed(TimerHandler pTimerHandler) {
                                cleanUpBubbles();
                            }
                        }));
    }

    private void cleanUpBubbles() {
        List<IEntity> bubbles = getAllBubbles();
        for (int i = 0 ; i < bubbles.size() ; i++) {
            IEntity bubble = bubbles.get(i);
            if (bubble.getY() > ScreenUtils.getSreenSize().heightPx * 1.2) {
                if (bubble instanceof IShape) {
                    Log.e("BubbleCleanerEntity", "Rogue bubble found at y = " + bubble.getY());
                    removeFromScene((IShape) bubble);
                }
            }
        }
    }

    /**
     * Returns all the bubbles present in the scene
     */
    private List<IEntity> getAllBubbles() {
        return scene.query(new BubblesEntityMatcher(false, false));
    }
}
