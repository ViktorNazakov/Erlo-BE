package com.example.Erlo.controller;

import com.example.Erlo.model.Course;
import com.example.Erlo.model.JwtUser;
import com.example.Erlo.model.Role;
import com.example.Erlo.service.JwtUserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class Controller {

    @Autowired
    private JwtUserService jwtUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user")
    public String user() {
        return "Hello user!";
    }

    @PostMapping("/save")
    public String register(@RequestBody JwtUser jwtUser) throws MessagingException, UnsupportedEncodingException {
        Optional<JwtUser> temp = jwtUserService.findJwtUserByEmail(jwtUser.getEmail());
        if (temp.isPresent()){
            if (jwtUser.getEmail().equals(temp.get().getEmail())){
                return "Email already taken";
            }
        }
        if(jwtUser.getUsername().trim().length() > 0 && jwtUser.getPassword().trim().length() > 0 && jwtUser.getEmail().trim().length() > 0) {
            String randomCode = RandomString.make(64);
            jwtUser.setVerificationCode(randomCode);
            jwtUser.setEnabled(false);
            jwtUserService.sendVerificationEmail(jwtUser);

            jwtUser.setRole(Set.of(Role.ROLE_USER));
            jwtUser.setPassword(passwordEncoder.encode(jwtUser.getPassword()));
            jwtUserService.save(jwtUser);
            return "registered";
        }
        return "Invalid info";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Hello admin!";
    }

    @GetMapping("/da")
    public String da(@RequestBody Course course, HttpServletRequest httpServletRequest) {
        JwtUser user = jwtUserService.getJwtUserByUsername(httpServletRequest.getUserPrincipal().getName());
        if (user.getCourse().contains(course)) {
            return "error";
        }
        user.setCourse(Set.of(course));
        return "da";
    }

    @GetMapping("/all")
    public List<JwtUser> all() {
        return jwtUserService.findAll();
    }

    @GetMapping("/info")
    public JwtUser getInfo(HttpServletRequest httpServletRequest) {
        return jwtUserService.getJwtUserByUsername(httpServletRequest.getUserPrincipal().getName());
    }
    @GetMapping("/checkForAuth")
    public String checkForAuth(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getUserPrincipal() == null) {
            return "error";
        }
        return "ok";
    }
    @PostMapping("/enroll")
    public String enroll(@RequestBody String course, HttpServletRequest httpServletRequest) {
        JwtUser user = jwtUserService.getJwtUserByUsername(httpServletRequest.getUserPrincipal().getName());
        if (user.getCourse().contains(Course.valueOf(course))) {
            return "error";
        }
        user.getCourse().add(Course.valueOf(course));
        jwtUserService.save(user);
        return "ok";
    }

    @PostMapping("/verify")
    public String verifyAccount(@RequestBody String code) {
        boolean verified = jwtUserService.verify(code);
        return verified ? "ok" : "no";
    }

}
