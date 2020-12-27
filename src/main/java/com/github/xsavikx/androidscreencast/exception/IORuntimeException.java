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

package com.github.xsavikx.androidscreencast.exception;

import java.io.IOException;

/**
 * Runtime Exception wrapper for {@link IOException}
 */
public final class IORuntimeException extends AndroidScreenCastRuntimeException {

    public IORuntimeException(String message, IOException cause) {
        super(message, cause);
    }

    public IORuntimeException(IOException cause) {
        super(cause);
    }

    public IORuntimeException(String message, IOException cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
