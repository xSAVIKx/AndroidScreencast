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
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

import static com.github.xsavikx.androidscreencast.api.recording.atom.AtomType.WIDE;

/**
 * WideDataAtom can grow larger then 4 gigabytes.
 */
public final class WideDataAtom extends DataAtom {

    private static final int HEADER_SIZE = 2;

    /**
     * Creates a new DataAtom at the current position of the ImageOutputStream.
     *
     * @param type The type of the atom.
     */
    public WideDataAtom(AtomType type, ImageOutputStream imageOutputStream) {
        super(type, imageOutputStream);
    }

    @Override
    protected int getHeaderElements() {
        return HEADER_SIZE;
    }

    @Override
    public void finish() {
        if (!finished) {
            try {
                long pointer = out.getStreamPosition();
                out.seek(offset);
                try (DataAtomOutputStream headerData = new DataAtomOutputStream(new FilterImageOutputStream(out))) {
                    long size = size();
                    if (size <= MAXIMUM_ATOM_SIZE) {
                        headerData.writeUInt(HEADER_ELEMENT_SIZE);
                        headerData.writeType(WIDE);
                        headerData.writeUInt(size);
                        headerData.writeType(type);
                    } else {
                        headerData.writeInt(1); // special value for extended
                        // size
                        // atoms
                        headerData.writeType(type);
                        headerData.writeLong(size);
                    }

                    out.seek(pointer);
                    finished = true;
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public long size() {
        long size = HEADER_ELEMENT_SIZE + data.size();
        return (size > MAXIMUM_ATOM_SIZE) ? size + HEADER_ELEMENT_SIZE : size;
    }
}
