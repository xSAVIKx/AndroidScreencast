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

package com.github.xsavikx.androidscreencast.api.injector;

import com.android.ddmlib.IShellOutputReceiver;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import java.io.IOException;
import java.io.OutputStream;

public final class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

    private final OutputStream os;

    public OutputStreamShellOutputReceiver(OutputStream os) {
        this.os = os;
    }

    @Override
    public void addOutput(byte[] buf, int off, int len) {
        try {
            os.write(buf, off, len);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            os.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
