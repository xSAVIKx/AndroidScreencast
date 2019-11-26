package com.github.xsavikx.androidscreencast.api.recording.atom;

import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

abstract class CommonAtom extends Atom {

    protected static final long HEADER_ELEMENT_SIZE = 8;

    public CommonAtom(AtomType type, ImageOutputStream imageOutputStream) {
        super(type, imageOutputStream);
        try {
            int headerSize = getHeaderElements();
            for (int i = 0; i < headerSize; i++) {
                out.writeLong(0); // make room for the atom header
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected abstract int getHeaderElements();
}
