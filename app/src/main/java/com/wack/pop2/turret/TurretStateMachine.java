package com.wack.pop2.turret;

import static com.wack.pop2.turret.TurretStateMachine.State.DOCKED;
import static com.wack.pop2.turret.TurretStateMachine.State.TARGETING;

class TurretStateMachine {

    public enum State {
        DOCKED, // The turret is not in play
        TARGETING, // The turret is looking for a new target
        FIRING, // The turret has found a target and is firing a new projectile
    }

    private State currentState = DOCKED;

    public TurretStateMachine() {}

    public boolean transitionState(State newState) {
        boolean isTransitionValid = false;
        switch (newState) {
            case DOCKED:
                if (newState == TARGETING){
                    isTransitionValid = true;
                }
                break;
            case TARGETING:
                break;
            case FIRING:
                break;
        }
        if (isTransitionValid) {
            currentState = newState;
        }
        return isTransitionValid;
    }
}
