package com.stupidfungames.pop.nuke;

import static com.stupidfungames.pop.nuke.NukeConstants.NUKE_DURATION_INTERVALS;
import static com.stupidfungames.pop.nuke.NukeConstants.NUKE_INTERVAL_SECONDS;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;

/**
 * Applies a nuke on all bubbles on the screen for NUKE_DURATION_SECONDS seconds
 */
public class NukerEntity extends BaseEntity {

  private TimerHandler nuke;

  /**
   * If a bubble has been queued up to be popped by the nuker then we add it here. If a bubble is
   * set to be popped that has already been queued up to be popped then we don't pop it again.
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
        engine.registerUpdateHandler(new TimerHandler(NUKE_INTERVAL_SECONDS,
            new NukeWaveTimerHandler(numWavesLeft - 1)));
      } else {
        bubbleNukeMutex.clear();
        engine.unregisterUpdateHandler(nuke);
        get(NukeStateMachine.class).transitionState(NukeStateMachine.State.COOLDOWN);
      }
    }

    private void popAllBubbles() {
      List<IEntity> bubbles = getAllBubbles();
      for (IEntity bubble : bubbles) {
        if (!bubbleNukeMutex.contains(bubble.hashCode())) {
          BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubble.getUserData();
          get(BubblePopperEntity.class)
              .popBubble(bubbleEntityUserData.bubbleSprite, bubbleEntityUserData.size,
                  bubbleEntityUserData.bubbleType);
          bubbleNukeMutex.add(bubble.hashCode());
        }
      }
    }

    private List<IEntity> getAllBubbles() {
      return scene.query(new BubblesEntityMatcher(false, true));
    }
  }

  public NukerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {

  }

  public void startNuke() {
    get(GameSoundsManager.class).getSound(SoundId.NUKE_START).play();
    get(NukeStateMachine.class).transitionState(NukeStateMachine.State.NUKING);
    nuke = new TimerHandler(NUKE_INTERVAL_SECONDS,
        new NukeWaveTimerHandler(NUKE_DURATION_INTERVALS));
    Log.d("asdasd", "startNuke registerUpdateHandler");
    scene.postRunnable(new Runnable() {
      @Override
      public void run() {
        engine.registerUpdateHandler(nuke);
      }
    });
  }
}
