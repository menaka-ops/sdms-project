package com.example.sdms.service;

import com.example.sdms.model.Results; // Changed to singular
import com.example.sdms.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Send notification when a student registers
    public void sendStudentRegistrationNotification(Student student) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("🎓 Welcome to SDMS - Registration Successful");

            message.setText("Hi " + student.getName() + ",\n\n"
                    + "Welcome to the Student Database Management System (SDMS)!\n"
                    + "Your registration has been completed successfully.\n\n"
                    + "You can now log in using your email (" + student.getEmail() + ") and the password you set.\n\n"
                    + "Best regards,\n"
                    + "SDMS Support Team");

            mailSender.send(message);
            System.out.println("✅ Registration email sent successfully to: " + student.getEmail());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send registration email: " + e.getMessage());
        }
    }

    // ✅ Send notification when exam results are published
    // UPDATED to send a full summary instead of one subject
    public void sendResultNotification(Student student, Results result) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("📢 SDMS Result Announcement!");

            // Build a result string
            String resultDetails = String.format(
                    "Hi %s,\n\n" +
                            "Your results for Semester %s have been published.\n\n" +
                            "--- SEMESTER RESULTS ---\n" +
                            "Attendance: %s\n\n" +
                            "Subject Marks (out of 100):\n" +
                            "- DLM:   %d\n" +
                            "- CAO:   %d\n" +
                            "- EVS:   %d\n" +
                            "- DBMS:  %d\n" +
                            "- P&S:   %d\n" +
                            "- UHV:   %d\n" +
                            "- OOPS:  %d\n\n" +
                            "--- SUMMARY ---\n" +
                            "Total Marks: %d / 700\n" +
                            "Final Grade:   %s\n\n" +
                            "Keep up the good work and continue learning!\n\n" +
                            "Best wishes,\n" +
                            "SDMS Academic Team",
                    student.getName(),
                    student.getSemester() != null ? student.getSemester() : "N/A",
                    result.getAttendanceRecord() != null ? result.getAttendanceRecord() : "N/A",
                    result.getDlmMarks(),
                    result.getCaoMarks(),
                    result.getEvsMarks(),
                    result.getDbmsMarks(),
                    result.getPsMarks(),
                    result.getUhvMarks(),
                    result.getOopsMarks(),
                    result.getTotalMarks(),
                    result.getGrade()
            );

            message.setText(resultDetails);
            mailSender.send(message);
            System.out.println("✅ Result email sent successfully to: " + student.getEmail());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send result email: " + e.getMessage());
        }
    }
}