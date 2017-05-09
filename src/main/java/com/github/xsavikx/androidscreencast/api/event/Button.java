package com.github.xsavikx.androidscreencast.api.event;

/**
 * Represents Button event codes
 */
public enum Button implements EventCode {
    BTN_MISC(0x100),
    BTN_0(0x100),
    BTN_1(0x101),
    BTN_2(0x102),
    BTN_3(0x103),
    BTN_4(0x104),
    BTN_5(0x105),
    BTN_6(0x106),
    BTN_7(0x107),
    BTN_8(0x108),
    BTN_9(0x109),

    BTN_MOUSE(0x110),
    BTN_LEFT(0x110),
    BTN_RIGHT(0x111),
    BTN_MIDDLE(0x112),
    BTN_SIDE(0x113),
    BTN_EXTRA(0x114),
    BTN_FORWARD(0x115),
    BTN_BACK(0x116),
    BTN_TASK(0x117),

    BTN_JOYSTICK(0x120),
    BTN_TRIGGER(0x120),
    BTN_THUMB(0x121),
    BTN_THUMB2(0x122),
    BTN_TOP(0x123),
    BTN_TOP2(0x124),
    BTN_PINKIE(0x125),
    BTN_BASE(0x126),
    BTN_BASE2(0x127),
    BTN_BASE3(0x128),
    BTN_BASE4(0x129),
    BTN_BASE5(0x12a),
    BTN_BASE6(0x12b),
    BTN_DEAD(0x12f),

    BTN_GAMEPAD(0x130),
    BTN_SOUTH(0x130),
    BTN_A(BTN_SOUTH),
    BTN_EAST(0x131),
    BTN_B(BTN_EAST),
    BTN_C(0x132),
    BTN_NORTH(0x133),
    BTN_X(BTN_NORTH),
    BTN_WEST(0x134),
    BTN_Y(BTN_WEST),
    BTN_Z(0x135),
    BTN_TL(0x136),
    BTN_TR(0x137),
    BTN_TL2(0x138),
    BTN_TR2(0x139),
    BTN_SELECT(0x13a),
    BTN_START(0x13b),
    BTN_MODE(0x13c),
    BTN_THUMBL(0x13d),
    BTN_THUMBR(0x13e),

    BTN_DIGI(0x140),
    BTN_TOOL_PEN(0x140),
    BTN_TOOL_RUBBER(0x141),
    BTN_TOOL_BRUSH(0x142),
    BTN_TOOL_PENCIL(0x143),
    BTN_TOOL_AIRBRUSH(0x144),
    BTN_TOOL_FINGER(0x145),
    BTN_TOOL_MOUSE(0x146),
    BTN_TOOL_LENS(0x147),
    BTN_TOOL_QUINTTAP(0x148),
    BTN_TOUCH(0x14a),
    BTN_STYLUS(0x14b),
    BTN_STYLUS2(0x14c),
    BTN_TOOL_DOUBLETAP(0x14d),
    BTN_TOOL_TRIPLETAP(0x14e),
    BTN_TOOL_QUADTAP(0x14f),

    BTN_WHEEL(0x150),
    BTN_GEAR_DOWN(0x150),
    BTN_GEAR_UP(0x151),
    STUB(Integer.MAX_VALUE);
    private final int code;

    Button(int code) {
        this.code = code;
    }

    Button(Button alias) {
        this.code = alias.code;
    }

    public static EventCode forCode(int code) {
        for (Button b : values()) {
            if (b.code == code) {
                return b;
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
        for (Button b : values()) {
            if (b.code == code) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
