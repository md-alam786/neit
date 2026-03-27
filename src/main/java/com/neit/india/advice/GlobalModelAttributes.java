package com.neit.india.advice;

import com.neit.india.entity.Student;
import com.neit.india.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {
    @Autowired
    private StudentRepository studentRepository;

    @ModelAttribute("student")
    public Student addStudentToModel(Principal principal) {
        if (principal != null) {
            return studentRepository.findByUsername(principal.getName()).orElse(null);
        }
        return null;
    }
}