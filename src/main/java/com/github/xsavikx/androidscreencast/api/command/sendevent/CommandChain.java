package com.github.xsavikx.androidscreencast.api.command.sendevent;

import com.github.xsavikx.androidscreencast.api.command.sendevent.event.Event;

import java.util.LinkedList;
import java.util.List;

public class CommandChain {
    private static final String SEND_EVENT = "sendevent";
    private static final String AND = " && ";
    private static final int AND_LENGTH = AND.length();
    private final List<String> chain;
    private final String inputEvent;
    private final String sendEventToInput;

    public CommandChain(String inputEvent) {
        this.inputEvent = inputEvent;
        chain = new LinkedList<>();
        sendEventToInput = SEND_EVENT + ' ' + inputEvent;
    }

    public void add(Event event) {
        String sb = sendEventToInput + ' ' + event.getType() +
                ' ' + event.getCode() +
                ' ' + event.getValue();
        chain.add(sb);
    }

    public String formatChain() {
        if (chain.size() == 0) {
            return "";
        } else if (chain.size() == 1) {
            return chain.get(0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String command : chain) {
            stringBuilder.append(command).append(AND);
        }
        String result = stringBuilder.substring(0, stringBuilder.length() - AND_LENGTH);
        return result;
    }
}
