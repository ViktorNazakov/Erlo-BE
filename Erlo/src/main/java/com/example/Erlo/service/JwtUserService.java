package com.example.Erlo.service;

import com.example.Erlo.model.JwtUser;
import com.example.Erlo.repository.JwtUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserService {

    @Autowired
    private JwtUserRepo jwtUserRepo;

    @Autowired
    private JavaMailSender javaMailSender;

    public JwtUser save(JwtUser user) {
        return jwtUserRepo.save(user);
    }

    public List<JwtUser> findAll() {
        return jwtUserRepo.findAll();
    }

    public Optional<JwtUser> findJwtUserByEmail(String email) {
        return jwtUserRepo.findJwtUserByEmail(email);
    }

    public JwtUser getJwtUserByEmail(String email) {
        return jwtUserRepo.findJwtUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("user not found by email!"));
    }

    public JwtUser getJwtUserByUsername(String username) {
        return jwtUserRepo.findJwtUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("user not found by username"));
    }
    public void sendVerificationEmail(JwtUser user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Please verify your registration";
        String senderName = "Erlo Academy";
        String mailContent = "<p>Dear " + user.getUsername() + " </p>";
        mailContent += "<p> Please click the link below to verify your registration:</p>";

        String verifyURL = "http://127.0.0.1:5500/Grood-FE/verify.html?code=" + user.getVerificationCode();
        mailContent += "<h3><a href ="+ verifyURL + ">VERIFY</a></h3>";
        mailContent += "<p>Thank you<br>Erlo Academy</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("erlo.academy@gmail.com",senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true);

        javaMailSender.send(message);

    }

    public boolean verify(String verificationCode) {
        JwtUser user = jwtUserRepo.findByVerificationCode(verificationCode);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setEnabled(true);
            jwtUserRepo.save(user);
            return true;
        }
    }
}
