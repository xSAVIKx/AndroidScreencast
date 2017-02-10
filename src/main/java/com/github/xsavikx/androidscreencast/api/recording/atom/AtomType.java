package com.github.xsavikx.androidscreencast.api.recording.atom;

/**
 * Represents all possible Atom types regarding
 * <a href="https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-SW1">Movie Atoms Apple docs</a>
 */
public enum AtomType {
    MDAT("mdat"),
    MOOV("moov"),
    MVHD("mvhd"),
    TRAK("trak"),
    TKHD("tkhd"),
    MDIA("mdia"),
    MDHD("mdhd"),
    HDLR("hdlr"),
    MHLR("mhlr"),
    VIDE("vide"),
    MINF("minf"),
    VMHD("vmhd"),
    ALIS("alis"),
    DINF("dinf"),
    DREF("dref"),
    STBL("stbl"),
    STSD("stsd"),
    RAW("raw "),
    JAVA("java"),
    JPEG("jpeg"),
    PNG("png "),
    STTS("stts"),
    STSC("stsc"),
    STSZ("stsz"),
    STCO("stco"),
    CO64("co64"),
    FTYP("ftyp"),
    QT("qt  "),
    WIDE("wide"),
    DHLR("dhlr");
    private final String stringRepresentation;

    AtomType(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String toStringRepresentation() {
        return stringRepresentation;
    }
}
