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


import com.github.xsavikx.androidscreencast.api.recording.DataAtomOutputStream;
import com.github.xsavikx.androidscreencast.api.recording.FilterImageOutputStream;
import com.github.xsavikx.androidscreencast.api.recording.exception.MaximumAtomSizeExeededException;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * A CompositeAtom contains an ordered list of Atoms.
 */
public final class CompositeAtom extends CommonAtom {

    private static final int HEADER_SIZE = 1;
    private final List<Atom> children;

    /**
     * Creates a new CompositeAtom at the current position of the ImageOutputStream.
     *
     * @param type The type of the atom.
     */
    public CompositeAtom(AtomType type, ImageOutputStream imageOutputStream) {
        super(type, imageOutputStream);
        children = newLinkedList();
    }

    @Override
    protected int getHeaderElements() {
        return HEADER_SIZE;
    }

    public void add(Atom child) {
        if (children.size() > 0) {
            children.get(children.size() - 1).finish();
        }
        children.add(child);
    }

    /**
     * Writes the atom and all its children to the ImageOutputStream and disposes of all resources held by the atom.
     */
    @Override
    public void finish() {
        try {
            if (!finished) {
                long size = size();
                if (size > MAXIMUM_ATOM_SIZE) {
                    throw new MaximumAtomSizeExeededException(MAXIMUM_ATOM_SIZE, size);
                }

                long pointer = out.getStreamPosition();
                out.seek(offset);
                try (DataAtomOutputStream headerData = new DataAtomOutputStream(new FilterImageOutputStream(out))) {
                    headerData.writeInt((int) size);
                    headerData.writeType(type);
                    for (Atom child : children) {
                        child.finish();
                    }
                    out.seek(pointer);
                    finished = true;
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long size() {
        long length = HEADER_ELEMENT_SIZE;
        for (Atom child : children) {
            length += child.size();
        }
        return length;
    }
}