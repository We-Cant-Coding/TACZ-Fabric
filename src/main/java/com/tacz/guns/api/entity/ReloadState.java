package com.tacz.guns.api.entity;

public class ReloadState {
    /**
     * The countdown will be -1 when there is no ammunition change.
     */
    public static final int NOT_RELOADING_COUNTDOWN = -1;
    /**
     * ammunition change state
     */
    protected ReloadState.StateType stateType;
    /**
     * Remaining duration of the reloading state, milliseconds
     */
    protected long countDown;

    public ReloadState() {
        stateType = StateType.NOT_RELOADING;
        countDown = NOT_RELOADING_COUNTDOWN;
    }

    public ReloadState(ReloadState src) {
        stateType = src.stateType;
        countDown = src.countDown;
    }

    /**
     * @return Returns the current status of the change.
     * It can be used to determine if the change is in progress, the stage of the change, etc.
     */
    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    /**
     * @return Returns NOT_RELOADING_COUNTDOWN(= -1) if StateType is NOT_RELOADING,
     * otherwise returns the remaining duration of the current state in ms.
     */
    public long getCountDown() {
        if (stateType == StateType.NOT_RELOADING) {
            return NOT_RELOADING_COUNTDOWN;
        }
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReloadState reloadState) {
            return reloadState.stateType.equals(stateType) && reloadState.countDown == countDown;
        } else {
            return false;
        }
    }

    public enum StateType {
        /**
         * Indicates that the current player has not made a bullet change.
         */
        NOT_RELOADING,
        /**
         * Indicates that the current change status is an empty chamber change in progress and is in the loading phase.
         */
        EMPTY_RELOAD_FEEDING,
        /**
         * Indicates that the current changeover status is empty and in the finalization phase.
         */
        EMPTY_RELOAD_FINISHING,
        /**
         * Indicates that the current change status is Tactical Quick Change in progress and is in the reload phase.
         */
        TACTICAL_RELOAD_FEEDING,
        /**
         * Indicates that the current changeover status is Tactical Rapid Change in progress and in the closeout phase.
         */
        TACTICAL_RELOAD_FINISHING;

        /**
         * Determine if this state is one of the stages in the process of shorting a position for a bullet.
         * This includes the closing stage of a short position for a bullet.
         */
        public boolean isReloadingEmpty() {
            return this == EMPTY_RELOAD_FEEDING || this == EMPTY_RELOAD_FINISHING;
        }

        /**
         * Determine if this state is one of the phases of the tactical ammunition change process.
         * This includes the closing phase of the tactical ammunition change.
         */
        public boolean isReloadingTactical() {
            return this == TACTICAL_RELOAD_FEEDING || this == TACTICAL_RELOAD_FINISHING;
        }

        /**
         * Determines if this state is one of the phases of the arbitrary bullet change process.
         * Includes the closing phase of an arbitrary bullet change.
         */
        public boolean isReloading() {
            return isReloadingEmpty() || isReloadingTactical();
        }

        /**
         * Determines if this state is the closing stage of an arbitrary bullet change process.
         */
        public boolean isReloadFinishing() {
            return this == StateType.EMPTY_RELOAD_FINISHING || this == StateType.TACTICAL_RELOAD_FINISHING;
        }
    }
}
