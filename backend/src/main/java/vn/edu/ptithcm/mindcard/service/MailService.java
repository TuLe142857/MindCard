package vn.edu.ptithcm.mindcard.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    /**
     * Send email(plain text)
     *
     * @param to receiver's email
     * @param subject email subject
     * @param plainText email content(plain text)
     */
    public void sendEmail(String to, String subject, String plainText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(plainText);
        mailSender.send(message);
    }

    /**
     * Send email with both plaint text and html content
     *
     * @param to receiver's email
     * @param subject email subject
     * @param plainText plaint content
     * @param htmlContent HTML content
     *
     * @throws MessagingException if multipart creation failed.
     */
    public void sendEmail(String to, String subject,
                          String plainText, String htmlContent)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(plainText, htmlContent);

        mailSender.send(message);
    }
}
