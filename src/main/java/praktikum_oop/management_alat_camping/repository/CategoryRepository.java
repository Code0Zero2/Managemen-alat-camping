/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.repository;

import praktikum_oop.management_alat_camping.config.DatabaseConfig;
import praktikum_oop.management_alat_camping.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author VICTUS
 */
public class CategoryRepository {
    public List<Category> findAll() throws SQLException {

        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories ORDER BY name";

        try (
                Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Category c = new Category();

                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));

                categories.add(c);
            }
        }

        return categories;
    }
}
