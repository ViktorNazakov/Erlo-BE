package com.example.Erlo;

import com.example.Erlo.model.Course;
import com.example.Erlo.model.JwtUser;
import com.example.Erlo.model.Role;
import com.example.Erlo.service.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TestUsers implements CommandLineRunner {

    @Autowired
    private JwtUserService jwtUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (jwtUserService.findJwtUserByEmail("admin@test.com").isEmpty()) {
            JwtUser u = jwtUserService.save(new JwtUser("Admin","admin@test.com",passwordEncoder.encode("admin"), true ,Set.of(Role.ROLE_ADMIN, Role.ROLE_USER), Set.of(Course.COURSE_BIOLOGY, Course.COURSE_CHEMISTRY, Course.COURSE_HISTORY)));
            jwtUserService.save(u);
        }
        if (jwtUserService.findJwtUserByEmail("user@test.com").isEmpty()) {
            JwtUser u = jwtUserService.save(new JwtUser("User","user@test.com",passwordEncoder.encode("user"), true ,Set.of(Role.ROLE_USER), Set.of(Course.COURSE_BIOLOGY)));
            jwtUserService.save(u);
        }
    }


}
