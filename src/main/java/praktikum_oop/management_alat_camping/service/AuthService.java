/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.service;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Worker;
import praktikum_oop.management_alat_camping.repository.WorkerRepository;
import java.sql.SQLException;

public class AuthService {
    private WorkerRepository workerRepo;

    public AuthService() {
        this.workerRepo = new WorkerRepository();
    }

    public Worker loginAsWorker(String username, String password) throws SQLException {
        Worker worker = workerRepo.findByUsername(username);
        if (worker != null && worker.getPassword().equals(password)) {
            return worker;
        }
        
        return null; // Login failed
    }
}