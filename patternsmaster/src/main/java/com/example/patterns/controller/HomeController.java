package com.example.patterns.controller;

import com.example.patterns.model.Charity;
import com.example.patterns.model.User;
import com.example.patterns.service.CharityService;
import com.example.patterns.service.EmailService;
import com.example.patterns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CharityService charityService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/")
    public String homePage(){
        return "mainpage";
    }
    @PreAuthorize("isAnonymous()")
    @GetMapping("/about")
    public String about(){
        return "aboutus";
    }
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login(){
        return "login";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model){
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        List<Charity> appliedCharities = charityService.getCharitiesByUser(user);
        model.addAttribute("appliedCharities", appliedCharities);
        List<Charity> charities = charityService.getCharities();
        model.addAttribute("charities", charities);
        return "profile";
    }
    @PreAuthorize("isAnonymous()")
    @GetMapping("/sign-up")
    public String signup(Model model){
        model.addAttribute("user", new User());
        return "index1";
    }

    @PostMapping("/sign-up")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        user.setRole("ROLE_USER");
        userService.registerUser(user);

        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String reset() {
        return "passwordReset";
    }
    @PostMapping("/reset-password")
    public String reset(@RequestParam String email) {
        String message = "You verified your email and now you can change";
        emailService.sendSimpleMessage(email, "Password Reset", message);
        System.out.println("mail sent..");
        return "redirect:/new-password";
    }
    @GetMapping("/letter")
        public String letter(){
        return "letterSend";
    }


    @PreAuthorize("isAnonymous()")
    @GetMapping("/new-password")
    public String newpassword(Model model){
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "newPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/new-password")
    public String submitNewPassword(@RequestParam("password") String newPassword,
                                    @RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);
        return "redirect:/profile";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply-to-charity")
    public String showApplyCharityForm() {
        return "applyCharity";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply-to-charity")
    public String applyCharity(@ModelAttribute Charity charity, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userService.getUserByEmail(userEmail);
            charity.setUser(user);

            charity.setDate(new Date());
            charity.setCollected(0);
            charity.setHandled(false);

            charityService.saveCharity(charity);
            return "redirect:/profile";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    @PostMapping("/handle-charity/{charityId}")
    public String handleCharity(@PathVariable Long charityId, Model model) {
        charityService.markHandled(charityId);
        return "redirect:/profile";
    }
    @PostMapping("/delete/{charityId}")
    public String delete(@PathVariable Long charityId, Model model) {

        charityService.deleteCharity(charityId);
        return "redirect:/profile";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/details/{charityId}")
    public String details(@PathVariable Long charityId, Model model) {
        Charity charity= charityService.getCharity(charityId);
        charity.setHandled(true);
        model.addAttribute("charity", charity);
        return "charityDetails";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all-charities")
    public String all(Model model) {
        model.addAttribute("allCharities", charityService.getCharities());
        return "allCharities";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{charityId}")
    public String detail(@PathVariable Long charityId, Model model) {
        Charity charity= charityService.getCharity(charityId);
        model.addAttribute("charity", charity);
        return "details";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/send-money/{charityId}")
    public String sendMoney(@PathVariable Long charityId,
                            @RequestParam("amount") int amount,
                            Model model) {
        Charity charity = charityService.getCharity(charityId);
        if (charity.getCollected() + amount <= charity.getGoal()) {
            charity.setCollected(charity.getCollected() + amount);
            charityService.saveCharity(charity);
        }
        return "redirect:/detail/" + charityId;
    }
}
