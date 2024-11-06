package com.vadym.board.services;

import com.vadym.board.models.Announcement;
import com.vadym.board.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendMessage(String sendTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(sendTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

    public void sendActivationCode(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "To activate your account, visit next link: http://localhost:8080/activate/%s",
                    user.getName(),
                    user.getActivationCode()
            );
            sendMessage(user.getEmail(), "Activation Code", message);
        }
    }

    public void notifySubscribers(Announcement announcement) {
        User announcingUser = announcement.getUser();
        Set<User> subscribers = announcingUser.getSubscribers();

        for (User subscriber : subscribers) {
            String subject = "New Announcement from " + announcingUser.getName() + " " + announcingUser.getSurname();
            String message = String.format(
                    "Hello, %s!\n\n%s has added a new announcement: \n\nVisit http://localhost:8080 to view it.",
                    subscriber.getName(),
                    announcingUser.getName()
            );
            sendMessage(subscriber.getEmail(), subject, message);
        }
    }
}
