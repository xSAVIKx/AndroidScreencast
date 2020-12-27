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

package com.github.xsavikx.androidscreencast.ui.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

abstract class LazyMutableTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = -6383034137965603498L;
    private boolean _loaded = false;

    LazyMutableTreeNode() {
        super();
    }

    public LazyMutableTreeNode(Object userObject) {
        super(userObject);
    }

    public LazyMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public void clear() {
        removeAllChildren();
        _loaded = false;
    }

    @Override
    public int getChildCount() {
        synchronized (this) {
            if (!_loaded) {
                _loaded = true;
                initChildren();
            }
        }
        return super.getChildCount();
    }

    protected abstract void initChildren();

    public boolean isLoaded() {
        return _loaded;
    }
}