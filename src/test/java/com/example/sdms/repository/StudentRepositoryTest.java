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

    // Find student by email
    Optional<Student> findByEmail(String email);

    // Find and delete by email
    void deleteByEmail(String email);

    // This query fixes "Error loading students" on the dashboard
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result")
    List<Student> findAllWithResults();

    // This query fixes "Student not found" on the EDIT page
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result WHERE s.id = :id")
    Optional<Student> findByIdWithResult(@Param("id") Long id);

    // This query fixes "Could not fetch" on the STUDENT dashboard
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.result WHERE s.email = :email")
    Optional<Student> findByEmailWithResult(@Param("email") String email);
}