package com.neit.india.controller;

import com.neit.india.entity.Student;
import com.neit.india.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;



@Controller
@RequestMapping("/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String username = principal.getName(); // logged-in username/email
        Student student = studentRepository.findByUsername(username)
                .orElse(null);

        model.addAttribute("student", student);
        return "home"; // template that includes header fragment
    }

    @GetMapping("/view")
    public String viewStudents(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "student-list"; // matches student-list.html in templates
    }

    // View a single student
    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable("id") Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        return "student-detail"; // student-detail.html
    }



    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        return "student-form"; // reuse student-form.html
    }

    // Handle update
    @PostMapping("/update")
    public String updateStudent(@ModelAttribute Student student) {
        studentRepository.save(student); // save() updates if ID exists
        return "redirect:/students/view";
    }

    // Delete student by ID
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        studentRepository.deleteById(id);
        return "redirect:/students/view"; // refresh list after deletion
    }

    @GetMapping("/students/filter")
    public String filterStudents(Model model) {
        List<Student> students = studentRepository.findByFirstNameStartingWithIgnoreCase("M");
        model.addAttribute("students", students);
        return "student-list";
    }

    @GetMapping("/students/filterr")
    public String filterrStudents(Model model) {
        List<Student> students = studentRepository.findByFirstNameStartingWithIgnoreCase("A");
        model.addAttribute("students", students);
        return "student-list";
    }

    //StudentRegistration Form
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "student-registration";
    }

    @PostMapping("/register")
    public String registerStudent(@RequestParam String firstName,
                                  @RequestParam String lastName,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
                                  @RequestParam String gender,
                                  @RequestParam String qualification,
                                  @RequestParam String address,
                                  @RequestParam String phone,
                                  @RequestParam String email,
                                  @RequestParam("photo") MultipartFile photo,
                                  @RequestParam("document") MultipartFile document,
                                  @RequestParam String documentType,
                                  Model model, RedirectAttributes redirectAttributes) throws IOException {

        // ✅ Check for duplicate email before saving
        if (studentRepository.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email already exists in database!");
            return "redirect:/students/register";
        }


        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDob(dob);
        student.setGender(gender);
        student.setQualification(qualification);
        student.setAddress(address);
        student.setPhone(phone);
        student.setEmail(email);
        student.setDocumentType(documentType);

        // Generate credentials
        String generatedUsername = student.getEmail();
        String rawPassword = UUID.randomUUID().toString().substring(0, 8);
        student.setUsername(generatedUsername);
        student.setPassword(passwordEncoder.encode(rawPassword));

        // Save student first to get ID
        student = studentRepository.save(student);

        // Create upload directories if not exist
        Path photoDir = Paths.get("uploads/photos");
        Path docDir = Paths.get("uploads/docs");
        Files.createDirectories(photoDir);
        Files.createDirectories(docDir);

        // Generate unique filenames
//        String photoFileName = student.getStudentId() + "_photo_" + photo.getOriginalFilename();
//        String docFileName = student.getStudentId() + "_doc_" + document.getOriginalFilename();
        String photoFileName = student.getStudentId() + "_photo_" + UUID.randomUUID() +
                photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf("."));
        String docFileName = student.getStudentId() + "_doc_" + UUID.randomUUID() +
                document.getOriginalFilename().substring(document.getOriginalFilename().lastIndexOf("."));

        Path photoPath = photoDir.resolve(photoFileName);
        Path docPath = docDir.resolve(docFileName);

        // Save files
        Files.copy(photo.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(document.getInputStream(), docPath, StandardCopyOption.REPLACE_EXISTING);

        // Update student record with file paths
//        student.setPhotoPath(photoPath.toString());
//        student.setDocumentPath(docPath.toString());
        student.setPhotoPath("/uploads/photos/" + photoFileName);
        student.setDocumentPath("/uploads/docs/" + docFileName);

        studentRepository.save(student); // update with file paths


      /*  model.addAttribute("successMessage", "Student " + firstName + " registered successfully!");
*/

// Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yourgmail@gmail.com"); // must match spring.mail.username
        message.setTo(student.getEmail());
        message.setSubject("Your Student Login Credentials");
        message.setText("Dear " + student.getFirstName() + ",\n\n" +
                "Your account has been created successfully.\n" +
                "Username: " + generatedUsername + "\n" +
                "Password: " + rawPassword + "\n\n" +
                "Please log in at http://localhost:8080/login and change your password after first login.\n\n" +
                "Regards,\nNEIT India");
        mailSender.send(message);

        redirectAttributes.addFlashAttribute("successMessage",
                "Student registered successfully! Login credentials have been sent to " + student.getEmail());


        /*   redirectAttributes.addFlashAttribute("successMessage", "Form has been submitted successfully!");
     */   return "redirect:/home"; // or redirect:/students/view if you want list page

    }


}
