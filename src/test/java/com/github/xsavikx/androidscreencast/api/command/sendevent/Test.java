package com.github.xsavikx.androidscreencast.api.command.sendevent;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.github.xsavikx.androidscreencast.api.adb.AndroidDebugBridgeWrapper;
import com.github.xsavikx.androidscreencast.api.command.sendevent.event.*;
import com.github.xsavikx.androidscreencast.api.injector.MultiLineReceiverPrinter;

import java.io.IOException;
import java.util.Random;


public class Test {
    private static final String ADB_PATH = "adb\\windows\\adb.exe";

    public static void main(String[] args) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {

        AndroidDebugBridgeWrapper bridge = new AndroidDebugBridgeWrapper(ADB_PATH);
        while (!bridge.hasInitialDeviceList()) ;
        IDevice[] devices = bridge.getDevices();
        final IDevice device = devices[0];
        MultiLineReceiverPrinter r = new MultiLineReceiverPrinter();
        CommandChain ch = new CommandChain("/dev/input/event1");
        Random random = new Random();
        final int TRACKING_ID = random.nextInt(32000);

        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_TRACKING_ID).withValue(TRACKING_ID).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_TOUCH_MAJOR).withValue(0x5).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_POSITION_X).withValue(0x179).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_POSITION_Y).withValue(0x23d).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_PRESSURE).withValue(0x2a).build());
        ch.add(Event.newBuilder().withType(Type.EV_SYN).withCode(SynchronizationReport.SYN_REPORT).withValue(Valueable.MIN_VALUE).build());

        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_TOUCH_MAJOR).withValue(0x1).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_PRESSURE).withValue(0x15).build());
        ch.add(Event.newBuilder().withType(Type.EV_ABS).withCode(AbsoluteAxes.ABS_MT_TRACKING_ID).withValue(Valueable.MAX_VALUE).build());
        ch.add(Event.newBuilder().withType(Type.EV_SYN).withCode(SynchronizationReport.SYN_REPORT).withValue(Valueable.MIN_VALUE).build());
        String commandChain = ch.formatChain();
        System.out.println("Sending:\n" + commandChain);

        device.executeShellCommand(commandChain, r);


    }
}
