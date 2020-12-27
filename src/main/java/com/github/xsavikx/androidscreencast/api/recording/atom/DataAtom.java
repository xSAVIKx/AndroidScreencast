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
import com.google.common.base.Preconditions;
import org.slf4j.Logger;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Data Atom.
 */
public class DataAtom extends CommonAtom {

    private static final int HEADER_SIZE = 1;
    protected final DataAtomOutputStream data;

    /**
     * Creates a new DataAtom at the current position of the ImageOutputStream.
     *
     * @param type The type of the atom.
     */
    public DataAtom(AtomType type, ImageOutputStream imageOutputStream) {
        super(type, imageOutputStream);
        data = new DataAtomOutputStream(new FilterImageOutputStream(out));
    }

    @Override
    protected int getHeaderElements() {
        return HEADER_SIZE;
    }

    @Override
    public void finish() {
        if (!finished) {
            try {
                long sizeBefore = size();
                if (sizeBefore > MAXIMUM_ATOM_SIZE) {
                    throw new MaximumAtomSizeExeededException(MAXIMUM_ATOM_SIZE, sizeBefore);
                }

                long pointer = out.getStreamPosition();
                out.seek(offset);
                try (DataAtomOutputStream headerData = new DataAtomOutputStream(new FilterImageOutputStream(out))) {
                    headerData.writeUInt(sizeBefore);
                    headerData.writeType(type);
                    out.seek(pointer);
                    finished = true;
                    long sizeAfter = size();
                    if (sizeBefore != sizeAfter) {
                        log().warn("Size mismatch. sizeBefore={}, sizeAfter={}.", sizeBefore, sizeAfter);
                    }
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * Returns the offset of this atom to the beginning of the random access file
     *
     * @return
     */
    public long getOffset() {
        return offset;
    }

    public DataAtomOutputStream getOutputStream() {
        Preconditions.checkState(!finished, "DataAtom is already finished.");
        return data;
    }

    @Override
    public long size() {
        return HEADER_ELEMENT_SIZE + data.size();
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(DataAtom.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}