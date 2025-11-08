package com.example.sdms.repository;

import com.example.sdms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Check if email already exists
    boolean existsByEmail(String email);

    // Find student by email (used mainly by Spring Security for login)
    Optional<Student> findByEmail(String email);

    // Find and delete by email
    void deleteByEmail(String email);

    // ✅ FIX 1: Fetch all students with their results (used by Teacher Dashboard)
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result")
    List<Student> findAllWithResults();

    // ✅ FIX 2: Fetch a single student with their results (used by Edit Page)
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result WHERE s.id = :id")
    Optional<Student> findByIdWithResult(@Param("id") Long id);

    // ✅ FIX 3: Fetch student by email with results (used by Student Dashboard)
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result WHERE s.email = :email")
    Optional<Student> findByEmailWithResult(@Param("email") String email);
}