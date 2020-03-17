package com.wack.pop2.bubbletimeout;

import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.audio.sound.Sound;

public class BubbleLifecycleSoundsManager implements BubbleLifecycleController, BaseStateMachine.Listener<BubbleLifeCycleStateMachine.State> {

    private BubbleLifeCycleStateMachine stateMachine;
    private GameSoundsManager soundsManager;

    public BubbleLifecycleSoundsManager(BubbleLifeCycleStateMachine stateMachine, GameSoundsManager soundsManager) {
        this.stateMachine = stateMachine;
        this.soundsManager = soundsManager;

        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        soundsManager.getSound(SoundId.BEEP).stop();
    }

    @Override
    public void onEnterState(BubbleLifeCycleStateMachine.State newState) {
        switch (newState) {
            case BLINKING_SLOWLY:
                Sound beepingSound = soundsManager.getSound(SoundId.BEEP);
                beepingSound.setLooping(true);
                beepingSound.play();
                break;
            case EXPLODING:
                soundsManager.getSound(SoundId.BEEP).stop();
                break;
        }
    }
}
