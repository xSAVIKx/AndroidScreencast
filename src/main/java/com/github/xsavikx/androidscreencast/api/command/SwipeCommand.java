package com.github.xsavikx.androidscreencast.api.command;

public class SwipeCommand extends InputCommand {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private long duration;

    public SwipeCommand(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public SwipeCommand(int x1, int y1, int x2, int y2, long duration) {
        this(x1, y1, x2, y2);
        this.duration = duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected String getCommandPart() {
        StringBuilder stringBuilder = new StringBuilder("swipe ");
        stringBuilder.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2);
        if (duration > 0) {
            stringBuilder.append(' ').append(duration);
        }
        return stringBuilder.toString();
    }

}
