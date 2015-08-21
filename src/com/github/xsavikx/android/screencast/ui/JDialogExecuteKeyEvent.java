package com.github.xsavikx.android.screencast.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.xsavikx.android.screencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.android.screencast.api.command.factory.AdbInputCommandFactory;
import com.github.xsavikx.android.screencast.api.injector.InputKeyEvent;
import com.github.xsavikx.android.screencast.ui.model.InputKeyEventTable;
import com.github.xsavikx.android.screencast.ui.model.InputKeyEventTableModel;

@Component
public class JDialogExecuteKeyEvent extends JDialog {
  private static final long serialVersionUID = -4152020879675916776L;
  private static final int HEIGHT = 600;
  private static final int WIDTH = 800;
  private static final int BUTTON_HEIGHT = 20;
  private static final int BUTTON_WIDTH = WIDTH >> 1 - 5;

  private static final int TITLE_COLUMN_INDEX = 1;

  private static final String EXECUTE_BUTTON_TEXT = "Execute";
  private static final String CANCEL_BUTTON_TEXT = "Cancel";
  private static final String COMMAND_LIST_TITLE_TEXT = "Commands to execute";
  private static final String NO_COMMAND_CHOSEN_WARNING_MESSAGE = "Please, select command from the list";
  private static final String NO_COMMAND_CHOSEN_WARNING_DIALOG_TITLE = "Warning";

  @Autowired
  private CommandExecutor commandExecutor;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new JDialogExecuteKeyEvent().setVisible(true);
      }
    });
  }

  /**
   * Create the dialog.
   */
  public JDialogExecuteKeyEvent() {
    setResizable(false);
    setTitle("Execute key event");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    final InputKeyEventTableModel commandList = new InputKeyEventTableModel(InputKeyEvent.values());
    final InputKeyEventTable commandListTable = new InputKeyEventTable(commandList);

    JButton executeCommandButton = new JButton(EXECUTE_BUTTON_TEXT);
    executeCommandButton.setSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    executeCommandButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int rowIndex = commandListTable.getSelectedRow();
        if (rowIndex > 0) {
          final String title = (String) commandList.getValueAt(rowIndex, TITLE_COLUMN_INDEX);
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              commandExecutor.execute(AdbInputCommandFactory.getKeyCommand(InputKeyEvent.valueOf(title)));
            }
          });
          closeDialog();
        } else {
          JOptionPane.showMessageDialog(null, NO_COMMAND_CHOSEN_WARNING_MESSAGE, NO_COMMAND_CHOSEN_WARNING_DIALOG_TITLE,
              JOptionPane.WARNING_MESSAGE);
        }
      }
    });
    JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        closeDialog();
      }
    });
    JScrollPane listScrollPane = new JScrollPane(commandListTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    listScrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    listScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        COMMAND_LIST_TITLE_TEXT, TitledBorder.CENTER, TitledBorder.TOP));
    JPanel buttonPane = new JPanel();
    buttonPane.add(executeCommandButton);
    buttonPane.add(cancelButton);
    buttonPane.setLayout(new GridLayout(1, 2));
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, buttonPane);
    splitPane.setEnabled(false);
    getContentPane().add(splitPane);
    pack();
    setLocationRelativeTo(null);
  }

  private void closeDialog() {
    setVisible(false);
  }
}
