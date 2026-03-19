package com.neit.india.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // matches login.html
    }

    @GetMapping("/")
    public String homePagee() {
        return "home"; // resolves to home.html
    }

    @GetMapping("/home")
    public String homePage() {
        return "home"; // resolves to home.html
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // resolves to about.html
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact"; // resolves to contact.html
    }

    @PostMapping("/contact")
    public String submitContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String message,
            Model model) {

        // For now, just show a confirmation message
        model.addAttribute("successMessage", "Thank you, " + name + ". We have received your message!");
        return "contact";
    }


}
