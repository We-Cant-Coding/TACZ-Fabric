package com.tacz.guns.api.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

public abstract class GunBaseEvent extends BaseEvent {
    public boolean post() {
        sendEvent();
        return isCanceled();
    }
}
