package app;

import app.ui.UserDashboard;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserDashboard::new);
    }
}
