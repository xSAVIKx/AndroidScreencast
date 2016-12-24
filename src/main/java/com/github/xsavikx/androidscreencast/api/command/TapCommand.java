package com.github.xsavikx.androidscreencast.api.command;

public class TapCommand extends InputCommand {
    private int x;
    private int y;

    public TapCommand(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected String getCommandPart() {
        return "tap " + x + ' ' + y;
    }
}
