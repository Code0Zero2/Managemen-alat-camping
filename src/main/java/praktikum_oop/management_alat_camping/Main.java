/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package praktikum_oop.management_alat_camping;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import praktikum_oop.management_alat_camping.view.MainFrame;

/**
 *
 * @author morxidia
 */

public class Main {

    public static void main(String[] args) {
        // 1. Optional: Set a clean, modern Look and Feel if you add FlatLaf to your pom.xml
        try {
            // If you don't use FlatLaf, this falls back to your operating system's native look
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Swing components are NOT thread-safe. Always initialize on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
//                frame.setVisible(true);
            }
        });
    }
}
