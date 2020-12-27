/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.xsavikx.androidscreencast.api.recording.atom;

import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Atom base class.
 */
abstract class Atom {

    static final long MAXIMUM_ATOM_SIZE = 0xffffffffL;

    /**
     * The type of the atom. A String with the length of 4 characters.
     */
    final AtomType type;
    protected final ImageOutputStream out;
    /**
     * The offset of the atom relative to the start of the ImageOutputStream.
     */
    long offset;
    /**
     * Shows whether current atom processing is finished or not
     */
    boolean finished;

    /**
     * Creates a new Atom at the current position of the ImageOutputStream.
     *
     * @param type The type of the atom. A string with a length of 4 characters.
     */
    Atom(AtomType type, ImageOutputStream imageOutputStream) {
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