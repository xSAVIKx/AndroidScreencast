package com.github.xsavikx.androidscreencast.api.command;

public abstract class InputCommand implements Command {
    private static final String TO_STRING_PATTERN = "%s [%s]";

    @Override
    public String toString() {
        return String.format(TO_STRING_PATTERN, this.getClass().getSimpleName(), getFormattedCommand());
    }

    @Override
    public String getFormattedCommand() {
        return INPUT + WHITESPACE + getCommandPart();
    }

    protected abstract String getCommandPart();
}
