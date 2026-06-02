/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.controller;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Worker;
import praktikum_oop.management_alat_camping.service.AuthService;
import praktikum_oop.management_alat_camping.view.LoginFrame;
import praktikum_oop.management_alat_camping.view.MainFrame;

import java.sql.SQLException;

public class LoginController {

    private final LoginFrame view;
    private final AuthService authService;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.authService = new AuthService();
    }

    public void handleLogin(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            view.showError("Username and password cannot be empty.");
            return;
        }

        view.setFormEnabled(false); // Prevent spam clicking

        try {
            Worker worker = authService.loginAsWorker(username, password);

            if (worker == null) {
                view.showError("Invalid Username or Password.");
                view.setFormEnabled(true);
                return;
            }

            if (!worker.getActive()) {
                view.showError("Your account is currently inactive. Contact Admin.");
                view.setFormEnabled(true);
                return;
            }

            view.dispose();

            new MainFrame(worker).setVisible(true);

        } catch (SQLException e) {
            view.showError("Database error: " + e.getMessage());
            view.setFormEnabled(true);
            e.printStackTrace();
        }
    }
}
