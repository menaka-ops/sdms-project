package com.example.sdms.service;

import com.example.sdms.model.Student;
import com.example.sdms.model.Teacher;
import com.example.sdms.repository.StudentRepository;
import com.example.sdms.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Try to find user as a Teacher
        Optional<Teacher> teacherOpt = teacherRepository.findByEmail(email);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_TEACHER");
            return new User(teacher.getEmail(), teacher.getPassword(), authorities);
        }

        // 2. Try to find user as a Student
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_STUDENT");
            return new User(student.getEmail(), student.getPassword(), authorities);
        }

        // 3. User not found
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}