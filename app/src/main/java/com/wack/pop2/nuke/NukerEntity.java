package com.wack.pop2.nuke;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.bubblepopper.BubblePopper;
import com.wack.pop2.bubblepopper.BufferedBubblePopperEntity;
import com.wack.pop2.entitymatchers.BubblesEntityMatcher;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wack.pop2.nuke.NukeConstants.NUKE_DURATION_INTERVALS;
import static com.wack.pop2.nuke.NukeConstants.NUKE_INTERVAL_SECONDS;

/**
 * Applies a nuke on all bubbles on the screen for NUKE_DURATION_SECONDS seconds
 */
public class NukerEntity extends BaseEntity {

    private TimerHandler nuke;
    private BubblePopper bubblePopperEntity;
    private NukeStateMachine nukeStateMachine;

    /**
     * If a bubble has been queued up to be popped by the nuker then we add it here.
     * If a bubble is set to be popped that has already been queued up to be popped then we don't
     * pop it again.
     */
    private Set<Integer> bubbleNukeMutex = new HashSet<>();

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
                bubbleNukeMutex.clear();
                engine.unregisterUpdateHandler(nuke);
                nukeStateMachine.transitionState(NukeStateMachine.State.COOLDOWN);
            }
        }

        private void popAllBubbles() {
            List<IEntity> bubbles = getAllBubbles();
            for (IEntity bubble : bubbles) {
                if (!bubbleNukeMutex.contains(bubble.hashCode())) {
                    BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubble.getUserData();
                    bubblePopperEntity.popBubble(bubbleEntityUserData.bubbleSprite, bubbleEntityUserData.size, bubbleEntityUserData.bubbleType);
                    bubbleNukeMutex.add(bubble.hashCode());
                }
            }
        }

        private List<IEntity> getAllBubbles() {
            return scene.query(new BubblesEntityMatcher(false, true));
        }
    }

    public NukerEntity(NukeStateMachine nukeStateMachine, BufferedBubblePopperEntity bubblePopperEntity, GameResources gameResources) {
        super(gameResources);
        this.nukeStateMachine =  nukeStateMachine;
        this.bubblePopperEntity = bubblePopperEntity;
    }

    public void startNuke() {
        nukeStateMachine.transitionState(NukeStateMachine.State.NUKING);
        nuke = new TimerHandler(NUKE_INTERVAL_SECONDS, new NukeWaveTimerHandler(NUKE_DURATION_INTERVALS));
        engine.registerUpdateHandler(nuke);
    }
}