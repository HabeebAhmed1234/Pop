package com.wack.pop2.nuke;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.entitymatchers.BubblesEntityMatcher;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;

import java.util.List;

import static com.wack.pop2.nuke.NukeConstants.NUKE_DURATION_INTERVALS;
import static com.wack.pop2.nuke.NukeConstants.NUKE_INTERVAL_SECONDS;

/**
 * Applies a nuke on all bubbles on the screen for NUKE_DURATION_SECONDS seconds
 */
public class NukerEntity extends BaseEntity {

    private TimerHandler nuke;
    private BubblePopperEntity bubblePopperEntity;

    private class NukeWaveTimerHandler implements ITimerCallback {

        private int numWavesLeft;

        public NukeWaveTimerHandler(int numWavesLeft) {
            this.numWavesLeft = numWavesLeft;
        }

        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
            if (numWavesLeft > 0) {
                popAllBubbles();
                engine.registerUpdateHandler(new TimerHandler(NUKE_INTERVAL_SECONDS, new NukeWaveTimerHandler(numWavesLeft - 1)));
            } else {
                engine.unregisterUpdateHandler(nuke);
            }
        }

        private void popAllBubbles() {
            List<IEntity> bubbles = getAllBubbles();
            for (IEntity bubble : bubbles) {
                BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubble.getUserData();
                bubblePopperEntity.popBubble(bubbleEntityUserData.bubbleSprite, bubbleEntityUserData.size, bubbleEntityUserData.bubbleType);
            }
        }

        private List<IEntity> getAllBubbles() {
            return scene.query(new BubblesEntityMatcher(false, true));
        }
    }

    public NukerEntity(BubblePopperEntity bubblePopperEntity, GameResources gameResources) {
        super(gameResources);
        this.bubblePopperEntity = bubblePopperEntity;
    }

    public void startNuke() {
        nuke = new TimerHandler(NUKE_INTERVAL_SECONDS, new NukeWaveTimerHandler(NUKE_DURATION_INTERVALS));
        engine.registerUpdateHandler(nuke);
    }
}
