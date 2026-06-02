/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package praktikum_oop.management_alat_camping;

/**
 *
 * @author morxidia
 */

// Main.java
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import praktikum_oop.management_alat_camping.view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
