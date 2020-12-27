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

import com.github.xsavikx.androidscreencast.api.AndroidDevice;
import com.github.xsavikx.androidscreencast.api.file.FileInfo;
import com.github.xsavikx.androidscreencast.exception.AndroidScreenCastRuntimeException;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JFrameExplorer extends JFrame {

    private static final long serialVersionUID = -5209265873286028854L;
    private final AndroidDevice androidDevice;
    private final JTree jt;
    private JList<Object> jListFichiers;
    private final Map<String, List<FileInfo>> cache = new LinkedHashMap<>();

    @Inject
    JFrameExplorer(AndroidDevice androidDevice) {

        setTitle("Explorer");
        setLayout(new BorderLayout());

        jt = new JTree(new DefaultMutableTreeNode("Test"));
        this.androidDevice = androidDevice;
    }

    public void launch() {

        jt.setModel(new DefaultTreeModel(new FolderTreeNode("Device", "/")));
        jt.setRootVisible(true);
        jt.addTreeSelectionListener(treeSelectionEvent -> {
            TreePath tp = treeSelectionEvent.getPath();
            if (tp == null)
                return;
            if (!(tp.getLastPathComponent() instanceof FolderTreeNode))
                return;
            FolderTreeNode node = (FolderTreeNode) tp.getLastPathComponent();
            displayFolder(node.path);
        });

        JScrollPane jsp = new JScrollPane(jt);

        jListFichiers = new JList<>();
        jListFichiers.setListData(new Object[]{});

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, new JScrollPane(jListFichiers));

        add(jSplitPane, BorderLayout.CENTER);
        setSize(640, 480);
        setLocationRelativeTo(null);

        jListFichiers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = jListFichiers.locationToIndex(e.getPoint());
                    ListModel<Object> dlm = jListFichiers.getModel();
                    FileInfo item = (FileInfo) dlm.getElementAt(index);
                    launchFile(item);
                }
            }
        });
    }

    private void displayFolder(String path) {
        List<FileInfo> fileInfos = cache.get(path);
        if (fileInfos == null)
            fileInfos = androidDevice.list(path);

        List<FileInfo> files = new ArrayList<>();
        for (FileInfo fi2 : fileInfos) {
            if (fi2.directory)
                continue;
            files.add(fi2);
        }
        jListFichiers.setListData(files.toArray());

    }

    private void launchFile(FileInfo node) {
        try {
            File tempFile = node.downloadTemporary();
            Desktop.getDesktop().open(tempFile);
        } catch (Exception ex) {
            throw new AndroidScreenCastRuntimeException(ex);
        }
    }

    private class FolderTreeNode extends LazyMutableTreeNode {
        private static final long serialVersionUID = 9131974430354670263L;
        private final String name;
        private final String path;

        FolderTreeNode(String name, String path) {
            this.name = name;
            this.path = path;
        }

        @Override
        public void initChildren() {
            List<FileInfo> fileInfos = cache.get(path);
            if (fileInfos == null)
                fileInfos = androidDevice.list(path);
            for (FileInfo fi : fileInfos) {
                if (fi.directory)
                    add(new FolderTreeNode(fi.name, path + fi.name + "/"));
                // else
                // add(new FileTreeNode(fi));
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
