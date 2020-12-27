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

package com.github.xsavikx.androidscreencast.configuration;

import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Singleton
public class ApplicationConfiguration {
    private static final String PROPERTIES_LOCATION = "./app.properties";
    private final Properties properties;

    @Inject
    public ApplicationConfiguration() {
        properties = initProperties();
    }

    private Properties initProperties() {
        final Properties properties = new Properties();
        loadProperties(properties, PROPERTIES_LOCATION);
        return properties;
    }

    private void loadProperties(final Properties properties, final String propertiesLocation) {
        final File propertiesFile = new File(propertiesLocation);
        if (propertiesFile.exists()) {
            try (final FileInputStream fis = new FileInputStream(propertiesFile)) {
                properties.load(fis);
            } catch (final IOException e) {
                throw new AndroidScreenCastRuntimeException(e);
            }
        }
    }

    public String getProperty(final ApplicationConfigurationProperty configurationProperty) {
        final String property = properties.getProperty(configurationProperty.getPropertyKey(), configurationProperty.getDefaultValue());
        return property;
    }
}
