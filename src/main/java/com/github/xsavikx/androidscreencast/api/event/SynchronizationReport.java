package com.github.xsavikx.androidscreencast.api.event;

public enum SynchronizationReport implements EventCode {
    SYN_REPORT(0x0),
    SYN_CONFIG(0x1),
    SYN_MT_REPORT(0x2),
    SYN_DROPPED(0x3),
    SYN_MAX(0xf),
    SYN_CNT(SYN_MAX.code + 1),
    STUB(Integer.MAX_VALUE);
    private final int code;

    SynchronizationReport(int keyCode) {
        this.code = keyCode;
    }

    public static EventCode forCode(int code) {
        for (SynchronizationReport r : values()) {
            if (r.code == code) {
                return r;
            }
        }
        return UNKNOWN;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public EventCode fromCode(int code) {
        for (SynchronizationReport r : values()) {
            if (r.code == code) {
                return r;
            }
        }
        return UNKNOWN;
    }
}
