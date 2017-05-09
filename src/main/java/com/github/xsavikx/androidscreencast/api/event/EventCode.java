package com.github.xsavikx.androidscreencast.api.event;

public interface EventCode extends Codeable {
    EventCode UNKNOWN = new EventCode() {
        @Override
        public EventCode fromCode(int code) {
            return UNKNOWN;
        }

        @Override
        public int getCode() {
            return Integer.MAX_VALUE;
        }
    };

    EventCode fromCode(int code);
}
