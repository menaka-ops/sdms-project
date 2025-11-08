package com.example.sdms.controller;

import com.example.sdms.model.Results;
import com.example.sdms.model.Student;
import com.example.sdms.repository.ResultRepository;
import com.example.sdms.repository.StudentRepository;
import com.example.sdms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, String>> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        Map<String, String> userDetailsMap = new HashMap<>();
        userDetailsMap.put("email", email);
        userDetailsMap.put("role", role);
        return ResponseEntity.ok(userDetailsMap);
    }

    @PostMapping("/auth/register-student")
    @Transactional
    public ResponseEntity<?> registerStudent(@RequestBody Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            return new ResponseEntity<>("Email already registered!", HttpStatus.BAD_REQUEST);
        }

        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            return new ResponseEntity<>("Password is required.", HttpStatus.BAD_REQUEST);
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        Results newResult = new Results();
        newResult.setStudent(student); // Link Result to Student
        student.setResult(newResult);  // Link Student to Result

        // Save the student. Cascade saves the result.
        Student savedStudent = studentRepository.save(student);

        try {
            notificationService.sendStudentRegistrationNotification(savedStudent);
        } catch (Exception e) {
            System.err.println("⚠️ Student registered, but email notification FAILED: " + e.getMessage());
        }
        return new ResponseEntity<>("Student registered successfully!", HttpStatus.CREATED);
    }

    // ✅ FIX: Use findAllWithResults to prevent LazyInitializationException
    @GetMapping("/teacher/students")
    public ResponseEntity<List<Student>> findAllStudents() {
        List<Student> students = studentRepository.findAllWithResults();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // ✅ FIX: Use findByIdWithResult to prevent LazyInitializationException on Edit page
    @GetMapping("/teacher/student/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findByIdWithResult(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/teacher/student/save")
    public ResponseEntity<?> saveOrUpdateStudent(@RequestBody Student studentData) {
        Student savedStudent;
        boolean isNewStudent = studentData.getId() == null;

        try {
            savedStudent = saveStudentWithTransaction(studentData);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving to database: " + e.getMessage());
        }

        if (isNewStudent) {
            try {
                notificationService.sendStudentRegistrationNotification(savedStudent);
            } catch (Exception e) {
                System.err.println("⚠️ Database save SUCCEEDED, but email notification FAILED: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(savedStudent);
    }

    @Transactional
    public Student saveStudentWithTransaction(Student studentData) {

        if (studentData.getId() != null) {
            // --- UPDATE LOGIC ---
            // Fetch by ID only to get the base student record
            Student existingStudent = studentRepository.findById(studentData.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // 1. Update all scalar student fields
            existingStudent.setName(studentData.getName());
            existingStudent.setEmail(studentData.getEmail());
            if (studentData.getPassword() != null && !studentData.getPassword().isEmpty()) {
                existingStudent.setPassword(passwordEncoder.encode(studentData.getPassword()));
            }
            existingStudent.setRollNumber(studentData.getRollNumber());
            existingStudent.setRegisterNumber(studentData.getRegisterNumber());
            existingStudent.setDob(studentData.getDob());
            existingStudent.setGender(studentData.getGender());
            existingStudent.setPhone(studentData.getPhone());
            existingStudent.setAddress(studentData.getAddress());
            existingStudent.setGuardianContact(studentData.getGuardianContact());
            existingStudent.setDepartment(studentData.getDepartment());
            existingStudent.setDegree(studentData.getDegree());
            existingStudent.setSemester(studentData.getSemester());
            existingStudent.setAdmissionYear(studentData.getAdmissionYear());
            existingStudent.setAdmissionNo(studentData.getAdmissionNo());
            existingStudent.setStudentStatus(studentData.getStudentStatus());
            existingStudent.setHostelStatus(studentData.getHostelStatus());
            existingStudent.setFeeDetails(studentData.getFeeDetails());

            // 2. Get the existing result or create a new one
            Results result = existingStudent.getResult();
            if (result == null) {
                result = new Results();
                result.setStudent(existingStudent); // Link it
                existingStudent.setResult(result); // Link it back
            }

            // 3. Update all result fields from the incoming data
            Results incomingResult = studentData.getResult();
            result.setAttendanceRecord(incomingResult.getAttendanceRecord());
            result.setDlmMarks(incomingResult.getDlmMarks());
            result.setCaoMarks(incomingResult.getCaoMarks());
            result.setEvsMarks(incomingResult.getEvsMarks());
            result.setDbmsMarks(incomingResult.getDbmsMarks());
            result.setPsMarks(incomingResult.getPsMarks());
            result.setUhvMarks(incomingResult.getUhvMarks());
            result.setOopsMarks(incomingResult.getOopsMarks());

            // 4. Save the student. Cascade will update the result.
            return studentRepository.save(existingStudent);

        } else {
            // --- CREATE LOGIC ---
            if (studentData.getPassword() == null || studentData.getPassword().isEmpty()) {
                throw new RuntimeException("Password is required for new student");
            }
            studentData.setPassword(passwordEncoder.encode(studentData.getPassword()));

            // 1. Get the 'result' object from the request
            Results result = studentData.getResult();

            // 2. Link them BOTH ways
            if (result != null) {
                result.setStudent(studentData); // Link result to student
                studentData.setResult(result); // Link student to result
            } else {
                // If no result object was sent, create a blank one
                Results newResult = new Results();
                newResult.setStudent(studentData);
                studentData.setResult(newResult);
            }

            // 3. Save the student. Cascade will save the 'result' object too.
            return studentRepository.save(studentData);
        }
    }

    @DeleteMapping("/teacher/student/{email}")
    @Transactional
    public ResponseEntity<String> deleteStudent(@PathVariable String email) {
        if (!studentRepository.existsByEmail(email)) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
        studentRepository.deleteByEmail(email);
        return ResponseEntity.ok("Student deleted successfully");
    }

    // ✅ FIX: Use findByEmailWithResult to prevent LazyInitializationException on Student Dashboard
    @GetMapping("/student/my-details")
    public ResponseEntity<?> getMyDetails(Authentication authentication) {
        String email = authentication.getName();

        Optional<Student> studentOpt = studentRepository.findByEmailWithResult(email);

        if (studentOpt.isEmpty()) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        Student student = studentOpt.get();
        return ResponseEntity.ok(student);
    }
}