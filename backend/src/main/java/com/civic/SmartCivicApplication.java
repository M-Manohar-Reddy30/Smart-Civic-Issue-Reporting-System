package com.civic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SmartCivicApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCivicApplication.class, args);
    }

    @Bean
    public CommandLineRunner migrateDatabase(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("Starting Database Migration & Security Setup...");
            
            String[] migrationSql = {
                "ALTER TABLE issues ADD COLUMN user_id INT",
                "ALTER TABLE users ADD COLUMN name VARCHAR(255)",
                "ALTER TABLE users ADD COLUMN role ENUM('CITIZEN', 'ADMIN') NOT NULL DEFAULT 'CITIZEN'",
                "ALTER TABLE users ADD COLUMN points INT DEFAULT 0",
                "ALTER TABLE users ADD COLUMN issues_count INT DEFAULT 0",
                "ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            };

            for (String sql : migrationSql) {
                try {
                    jdbcTemplate.execute(sql);
                } catch (Exception e) {
                    // Ignore already exists
                }
            }
            
            // Ensure Admin user exists with hashed password
            String adminEmail = "manoharreddyind@gmail.com";
            String hashedPassword = passwordEncoder.encode("Nani@8888");
            
            try {
                jdbcTemplate.update(
                    "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE password = ?, role = 'ADMIN'",
                    "Admin", adminEmail, hashedPassword, "ADMIN", hashedPassword
                );
                System.out.println("Admin security credentials updated/verified.");
            } catch (Exception e) {
                System.err.println("Error updating admin: " + e.getMessage());
            }

            System.out.println("Database Migration Finished.");
        };
    }
}
