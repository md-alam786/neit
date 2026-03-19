package com.neit.india.controller;

import com.neit.india.entity.User;
import com.neit.india.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin-superadmin")
public class AdminSuperadminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSuperadminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/view")
    public String viewUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-superadmin-list-view"; // resolves to admin-superadmin-list-view
    }


    @GetMapping("/create")
    public String showCreatePage() {
        return "admin-superadmin-create"; // resolves to admin-superadmin-create.html
    }


    @PostMapping("/create")
    public String createAdmin(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String role) {

        // check both email and username
        if (userRepository.existsByEmail(email)) {
            return "redirect:/admin-superadmin/create?error=email";
        }
        if (userRepository.existsByUsername(name)) {
            return "redirect:/admin-superadmin/create?error=username";
        }

        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);

        return "redirect:/students/view"; // redirect after success
    }
}
