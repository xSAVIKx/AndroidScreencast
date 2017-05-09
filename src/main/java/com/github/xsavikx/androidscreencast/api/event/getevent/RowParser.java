package com.github.xsavikx.androidscreencast.api.event.getevent;

import com.github.xsavikx.androidscreencast.api.event.*;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Parses Row of `getevent` command output into the {@link com.github.xsavikx.androidscreencast.api.event.Event} objects
 */
@Singleton
public class RowParser {
    private final EventType2CodeMapper mapper;

    @Inject
    public RowParser(EventType2CodeMapper mapper) {
        this.mapper = mapper;
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    /**
     * Parses given {@code row} into the {@code Event} object.
     * <p>
     * Given row should follow `getevent` output format:
     * <pre>
     *     type code    value
     *     0003 0039 000019ba
     *     0003 0036 00000246
     *     0003 003a 0000002a
     *     0001 014a 00000001
     * </pre>
     * Where all numbers are null-prefixed integers in HEX format.
     *
     * @param row of output to be parsed
     * @return Event object with filled type, code and value
     */
    public Event parse(String row) {
        String[] data = row.trim().split(" ");
        Preconditions.checkState(data.length == 3,
                "There should be exactly 3 parts in getevent row: type code value");
        EventType eventType = Type.forCode(Integer.parseInt(data[0], 16));
        EventCode eventCode = mapper.forType(eventType, Integer.parseInt(data[1], 16));
        int value = Integer.parseInt(data[2], 16);
        Event event = Event.newBuilder().withType(eventType).withCode(eventCode).withValue(value).build();
        return event;
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(RowParser.class);
    }
}
