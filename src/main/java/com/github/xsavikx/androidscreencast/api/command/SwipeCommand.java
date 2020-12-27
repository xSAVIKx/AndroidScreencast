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

package com.github.xsavikx.androidscreencast.api.command;

public final class SwipeCommand extends InputCommand {

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private long duration;

    public SwipeCommand(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public SwipeCommand(int x1, int y1, int x2, int y2, long duration) {
        this(x1, y1, x2, y2);
        this.duration = duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected String getCommandPart() {
        StringBuilder stringBuilder = new StringBuilder("swipe ");
        stringBuilder.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2);
        if (duration > 0) {
            stringBuilder.append(' ').append(duration);
        }
        return stringBuilder.toString();
    }
}
