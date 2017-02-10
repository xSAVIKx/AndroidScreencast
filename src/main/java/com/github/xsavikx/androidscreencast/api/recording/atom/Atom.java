package com.github.xsavikx.androidscreencast.api.recording.atom;

import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Atom base class.
 */
public abstract class Atom {
    protected static final long MAXIMUM_ATOM_SIZE = 0xffffffffL;

    /**
     * The type of the atom. A String with the length of 4 characters.
     */
    protected final AtomType type;
    protected final ImageOutputStream out;
    /**
     * The offset of the atom relative to the start of the ImageOutputStream.
     */
    protected long offset;
    /**
     * Shows whether current atom processing is finished or not
     */
    protected boolean finished;

    /**
     * Creates a new Atom at the current position of the ImageOutputStream.
     *
     * @param type The type of the atom. A string with a length of 4 characters.
     */
    public Atom(AtomType type, ImageOutputStream imageOutputStream) {
        checkNotNull(type, "Type should not be null");
        checkNotNull(imageOutputStream, "ImageOutputStream should not be null");
        this.out = imageOutputStream;
        this.type = type;
        try {
            offset = out.getStreamPosition();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes the atom to the ImageOutputStream and disposes it.
     */
    public abstract void finish();

    /**
     * Returns the size of the atom including the size of the atom header.
     *
     * @return The size of the atom.
     */
    public abstract long size();
}