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

package com.github.xsavikx.androidscreencast;

import com.github.xsavikx.androidscreencast.app.Application;
import com.github.xsavikx.androidscreencast.dagger.MainComponentProvider;
import org.slf4j.Logger;

import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        log().debug("Starting Android Screencast with the args: {}", Arrays.toString(args));
        try {
            Application application = MainComponentProvider.mainComponent().application();
            application.init();
            application.start();
        } finally {
            log().debug("The application started.");
        }
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(Main.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
