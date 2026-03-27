package com.neit.india.repository;

import com.neit.india.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByFirstNameStartingWithIgnoreCase(String prefix);

    List<Student> findByLastNameStartsWith(String postfix);

    Optional<Student> findByUsername(String username);
    Optional<Student> findByEmail(String email);

}
