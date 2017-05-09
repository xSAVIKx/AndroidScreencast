package com.github.xsavikx.androidscreencast.api.event;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class EventType2CodeMapper {
    private final Map<EventType, EventCode> mapping;

    @Inject
    public EventType2CodeMapper() {
        mapping = new HashMap<>();
        initMapping(mapping);
    }

    private void initMapping(Map<EventType, EventCode> mapping) {
        mapping.put(Type.EV_ABS, AbsoluteAxes.STUB);
        mapping.put(Type.EV_SYN, SynchronizationReport.STUB);
        mapping.put(Type.EV_KEY, Button.STUB);
    }

    public EventCode forType(EventType t, int code) {
        EventCode eventCode = mapping.get(t);
        if (eventCode == null) {
            return EventCode.UNKNOWN;
        }
        EventCode result = eventCode.fromCode(code);
        return result;
    }
}