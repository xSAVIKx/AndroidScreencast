package com.github.xsavikx.androidscreencast.api.event;

class EventImpl implements Event {
    private final EventCode code;
    private final EventType type;
    private final int value;

    EventImpl(EventCode code, EventType type, int value) {
        this.code = code;
        this.type = type;
        this.value = value;
    }

    @Override
    public int getCode() {
        return code.getCode();
    }

    @Override
    public int getType() {
        return type.getType();
    }

    @Override
    public int getValue() {
        return value;
    }


}
