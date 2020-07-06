package com.github.xsavikx.androidscreencast.api.command;

public final class TapCommand extends InputCommand {

    private final int x;
    private final int y;

    public TapCommand(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected String getCommandPart() {
        return "tap " + x + ' ' + y;
    }
}
