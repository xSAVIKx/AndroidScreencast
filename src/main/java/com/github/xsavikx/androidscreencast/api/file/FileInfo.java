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

package com.github.xsavikx.androidscreencast.api.file;

import com.github.xsavikx.androidscreencast.api.AndroidDeviceImpl;
import com.github.xsavikx.androidscreencast.exception.IORuntimeException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

@Singleton
public final class FileInfo {

    public AndroidDeviceImpl device;
    public String path;
    public String attribs;
    public boolean directory;
    public String name;

    @Inject
    public FileInfo() {
    }

    public File downloadTemporary() {
        try {
            File tempFile = File.createTempFile("android", name);
            device.pullFile(path + name, tempFile);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
