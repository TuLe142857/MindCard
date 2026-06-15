package vn.edu.ptithcm.mindcard.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;

@Slf4j
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
     *
     * @throws AppException with {@link ErrorCode}:
     * <ul>
     *     <li>{@link ErrorCode#SERVER_ERROR} mail send failed</li>
     * </ul>
     */
    public void sendEmail(String to, String subject, String plainText) throws AppException {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(plainText);
            mailSender.send(message);
        }catch (MailException e){
            log.error("Mail Send failed", e);
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Send email with both plaint text and html content
     *
     * @param to receiver's email
     * @param subject email subject
     * @param plainText plaint content
     * @param htmlContent HTML content
     *
     * @throws AppException with {@link ErrorCode}:
     * <ul>
     *     <li>{@link ErrorCode#SERVER_ERROR} mail send failed</li>
     * </ul>
     */
    public void sendEmail(String to, String subject,
                          String plainText, String htmlContent)
            throws AppException {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, htmlContent);

            mailSender.send(message);
        }catch (MessagingException | MailException e){
            log.error("Mail Send failed", e);
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, e.getMessage());
        }
    }
}
