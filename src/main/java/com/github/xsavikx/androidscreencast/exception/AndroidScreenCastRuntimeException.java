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

public class AndroidScreenCastRuntimeException extends RuntimeException {

    private String additionalInformation;

    public AndroidScreenCastRuntimeException() {
    }

    public AndroidScreenCastRuntimeException(String message) {
        super(message);
    }


    public AndroidScreenCastRuntimeException(String message, String additionalInformation) {
        super(message);
        this.additionalInformation = additionalInformation;
    }

    public AndroidScreenCastRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidScreenCastRuntimeException(Throwable cause) {
        super(cause);
    }

    public AndroidScreenCastRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }
}
