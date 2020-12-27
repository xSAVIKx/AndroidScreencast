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
