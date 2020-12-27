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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

@Singleton
public final class Injector {

    private final ScreenCaptureRunnable screenCaptureRunnable;
    private final Thread screenCaptureThread;

    @Inject
    public Injector(final ScreenCaptureRunnable screenCaptureRunnable) {
        this.screenCaptureRunnable = screenCaptureRunnable;
        this.screenCaptureThread = new Thread(screenCaptureRunnable, "Screen Capturer");
        this.screenCaptureThread.setDaemon(true);
    }

    public void stop() {
        screenCaptureRunnable.stop();
    }

    public void start() {
        screenCaptureThread.start();
    }

    public void setScreenCaptureListener(final ScreenCaptureRunnable.ScreenCaptureListener listener) {
        this.screenCaptureRunnable.setListener(listener);
    }

    public void startRecording(final File file) {
        screenCaptureRunnable.startRecording(file);
    }

    public void stopRecording() {
        screenCaptureRunnable.stopRecording();
    }

    public void toggleOrientation() {
        screenCaptureRunnable.toggleOrientation();
    }
}
