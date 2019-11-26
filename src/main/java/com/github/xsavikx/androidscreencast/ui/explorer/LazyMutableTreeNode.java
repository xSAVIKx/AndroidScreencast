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