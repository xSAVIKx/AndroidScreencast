package com.github.xsavikx.androidscreencast.api.event;

public interface Event extends Valueable, Codeable, Typeable {

    static Builder newBuilder() {
        return new Builder();
    }

    final class Builder {
        private EventCode code;
        private EventType type;
        private int value;

        private Builder() {
        }

        public Builder withCode(EventCode code) {
            this.code = code;
            return this;
        }

        public Builder withType(EventType type) {
            this.type = type;
            return this;
        }

        public Builder withValue(int value) {
            this.value = value;
            return this;
        }

        public Event build() {
            EventImpl eventImpl = new EventImpl(code, type, value);
            return eventImpl;
        }
    }
}
