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

package com.github.xsavikx.androidscreencast.app;

import com.github.xsavikx.androidscreencast.configuration.ApplicationConfiguration;
import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;
import com.github.xsavikx.androidscreencast.ui.JDialogError;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.github.xsavikx.androidscreencast.configuration.ApplicationConfigurationProperty.APP_NATIVE_LOOK;
import static org.slf4j.LoggerFactory.getLogger;

abstract class SwingApplication extends GUIApplication {

    private static final String SYNC_TREE_UI = "SynthTreeUI";
    protected final ApplicationConfiguration applicationConfiguration;
    private JDialogError jd = null;

    SwingApplication(final ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    private boolean useNativeLook() {
        return Boolean.parseBoolean(applicationConfiguration.getProperty(APP_NATIVE_LOOK));
    }

    @Override
    public void init() {
        try {
            if (useNativeLook())
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    @Override
    public void handleException(final Thread thread, final Throwable ex) {
        try {
            final StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            if (sw.toString().contains(SYNC_TREE_UI))
                return;
            log().error(ex.getClass().getSimpleName(), ex);
            if (jd != null && jd.isVisible())
                return;
            jd = new JDialogError(ex);
            SwingUtilities.invokeLater(() -> {
                jd.setVisible(true);
            });
        } catch (Exception e) {
            log().warn("Exception occurred during exception handling.", e);
        }
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings({"NonSerializableFieldInSerializableClass", "ImmutableEnumChecker"})
        private final Logger value = getLogger(SwingApplication.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
