package com.github.xsavikx.androidscreencast.api.event;

public interface EventType extends Typeable {
    EventType UNKNOWN = new EventType() {
        @Override
        public int getType() {
            return Integer.MAX_VALUE;
        }
    };
}
