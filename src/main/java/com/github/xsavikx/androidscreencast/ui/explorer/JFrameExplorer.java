package com.github.xsavikx.androidscreencast.ui.explorer;

import com.github.xsavikx.androidscreencast.api.AndroidDevice;
import com.github.xsavikx.androidscreencast.api.file.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Component
public class JFrameExplorer extends JFrame {

    private static final long serialVersionUID = -5209265873286028854L;
    private final AndroidDevice androidDevice;
    private JTree jt;
    private JSplitPane jSplitPane;
    private JList<Object> jListFichiers;
    private Map<String, List<FileInfo>> cache = new LinkedHashMap<>();

    @Autowired
    public JFrameExplorer(AndroidDevice androidDevice) {

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

        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, new JScrollPane(jListFichiers));

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

        List<FileInfo> files = new Vector<>();
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
            throw new RuntimeException(ex);
        }
    }

    private class FolderTreeNode extends LazyMutableTreeNode {
        private static final long serialVersionUID = 9131974430354670263L;
        String name;
        String path;

        public FolderTreeNode(String name, String path) {
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
