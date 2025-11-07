package com.example.sdms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Import
import com.example.sdms.model.Teacher;
import com.example.sdms.repository.TeacherRepository;

@SpringBootApplication
public class SdmsApplication implements CommandLineRunner {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the encoder

    public static void main(String[] args) {
        SpringApplication.run(SdmsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Create a default teacher only if one doesn't exist
        if (!teacherRepository.existsByEmail("teacher@sdms.com")) {
            Teacher t = new Teacher();
            t.setName("Default Teacher");
            t.setEmail("teacher@sdms.com");
            // ✅ Encode the password before saving
            t.setPassword(passwordEncoder.encode("12345"));
            t.setDepartment("Admin");
            teacherRepository.save(t);
            System.out.println("✅ Default teacher account created: teacher@sdms.com / 12345");
        } else {
            System.out.println("Teacher account already exists.");
        }
    }
}