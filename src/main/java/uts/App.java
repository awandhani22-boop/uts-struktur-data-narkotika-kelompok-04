package uts;

import uts.gui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {

        }
        ToolTipManager.sharedInstance().setInitialDelay(400);
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
