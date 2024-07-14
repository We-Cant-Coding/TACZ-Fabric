package com.tacz.guns.forge;

public class CancelableEvent {
    private boolean isCanceled = false;

    public boolean isCanceled() {
        return this.isCanceled;
    }

    public void setCanceled(boolean cancel) {
        this.isCanceled = cancel;
    }
}
