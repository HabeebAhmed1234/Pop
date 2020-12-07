package com.stupidfungames.pop.nuke;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import java.util.List;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

/**
 * Applies a nuke on all bubbles on the screen for NUKE_DURATION_SECONDS seconds
 */
public class NukerEntity extends BaseEntity {

  public static final int NUM_NUKES_STARTING = 1;
  public static final int NUM_NUKES_DELTA_PER_UPGRADE = 1;
  public static final int NUKE_INTERVAL_SECONDS = 1;

  private TimerHandler nuke;
  private int numNukes = NUM_NUKES_STARTING;

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
        engine.unregisterUpdateHandler(nuke);
        get(NukeStateMachine.class).transitionState(NukeStateMachine.State.COOLDOWN);
      }
    }

    private void popAllBubbles() {
      List<IEntity> bubbles = getAllBubbles();
      BubblePopperEntity bubblePopperEntity = get(BubblePopperEntity.class);
      for (IEntity bubble : bubbles) {
        bubblePopperEntity.popBubble((Sprite) bubble);
      }
    }

    private List<IEntity> getAllBubbles() {
      return scene.query(new BubblesEntityMatcher(false, true));
    }
  }

  public NukerEntity(BinderEnity parent) {
    super(parent);
  }

  public void onUpgrade(int upgradeLevel) {
    numNukes = NUM_NUKES_STARTING + upgradeLevel * NUM_NUKES_DELTA_PER_UPGRADE;
  }

  public void startNuke() {
    get(GameSoundsManager.class).getSound(SoundId.NUKE_START).play();
    get(NukeStateMachine.class).transitionState(NukeStateMachine.State.NUKING);
    nuke = new TimerHandler(NUKE_INTERVAL_SECONDS,
        new NukeWaveTimerHandler(numNukes));
    scene.postRunnable(new Runnable() {
      @Override
      public void run() {
        engine.registerUpdateHandler(nuke);
      }
    });
  }
}
