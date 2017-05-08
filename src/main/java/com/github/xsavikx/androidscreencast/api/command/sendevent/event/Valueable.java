package com.github.xsavikx.androidscreencast.api.command.sendevent.event;

public interface Valueable {
    int MIN_VALUE = 0x0;
    int MAX_VALUE = 0xffffffff;

    int getValue();
}
