/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */
import praktikum_oop.management_alat_camping.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginController controller;

    public LoginFrame() {
        this.controller = new LoginController(this);
        
        setTitle("Worker Login - Camping Management");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        // Action Listeners
        loginButton.addActionListener((ActionEvent e) -> triggerLogin());
        passwordField.addActionListener((ActionEvent e) -> triggerLogin()); // Allows hitting 'Enter'

        add(panel);
    }

    private void triggerLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        controller.handleLogin(username, password);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setFormEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
    }
}