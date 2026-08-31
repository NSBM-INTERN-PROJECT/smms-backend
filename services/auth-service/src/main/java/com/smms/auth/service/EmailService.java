package com.smms.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String name, String otpCode, int expiryMinutes) {
        String subject = "Your SMMS Login OTP Code";
        String html = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
                  <div style='background: #1a73e8; padding: 24px; text-align: center;'>
                    <h1 style='color: white; margin: 0;'>SMMS</h1>
                    <p style='color: #e8f0fe; margin: 4px 0 0;'>Student Mentoring Management System</p>
                  </div>
                  <div style='padding: 32px; background: #f8f9fa;'>
                    <p>Hello <strong>%s</strong>,</p>
                    <p>Your one-time login code is:</p>
                    <div style='background: white; border: 2px solid #1a73e8; border-radius: 8px;
                                padding: 24px; text-align: center; margin: 24px 0;'>
                      <span style='font-size: 40px; font-weight: bold; letter-spacing: 12px;
                                   color: #1a73e8;'>%s</span>
                    </div>
                    <p style='color: #666;'>This code expires in <strong>%d minutes</strong>.</p>
                    <p style='color: #666;'>If you did not request this code, please ignore this email.</p>
                  </div>
                  <div style='background: #e8eaed; padding: 16px; text-align: center;
                              font-size: 12px; color: #666;'>
                    <p>Do not share this code with anyone. SMMS will never ask for your OTP.</p>
                  </div>
                </div>
                """.formatted(name, otpCode, expiryMinutes);
        sendHtml(to, subject, html);
    }

    @Async
    public void sendWelcomeEmail(String to, String name, String temporaryPassword) {
        String subject = "Welcome to SMMS — Your Account is Ready";
        String html = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
                  <div style='background: #1a73e8; padding: 24px; text-align: center;'>
                    <h1 style='color: white; margin: 0;'>SMMS</h1>
                  </div>
                  <div style='padding: 32px; background: #f8f9fa;'>
                    <p>Hello <strong>%s</strong>, welcome to SMMS!</p>
                    <p>Your account has been created. Use these credentials to log in:</p>
                    <div style='background: white; border-left: 4px solid #1a73e8;
                                padding: 16px; margin: 16px 0;'>
                      <p><strong>Email:</strong> %s</p>
                      <p><strong>Temporary Password:</strong> <code>%s</code></p>
                    </div>
                    <p style='color: #e53935;'><strong>Important:</strong> You will be required
                    to change your password on first login.</p>
                    <p><a href='https://smms.vercel.app/login'
                          style='background: #1a73e8; color: white; padding: 12px 24px;
                                 border-radius: 4px; text-decoration: none;'>Login to SMMS</a></p>
                  </div>
                </div>
                """.formatted(name, to, temporaryPassword);
        sendHtml(to, subject, html);
    }

    @Async
    public void sendPasswordResetEmail(String to, String name, String temporaryPassword) {
        String subject = "SMMS — Your Password Has Been Reset";
        String html = """
                <div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>
                  <div style='background: #1a73e8; padding: 24px; text-align: center;'>
                    <h1 style='color: white; margin: 0;'>SMMS</h1>
                  </div>
                  <div style='padding: 32px; background: #f8f9fa;'>
                    <p>Hello <strong>%s</strong>,</p>
                    <p>Your password has been reset by an administrator.</p>
                    <div style='background: white; border-left: 4px solid #e53935;
                                padding: 16px; margin: 16px 0;'>
                      <p><strong>New Temporary Password:</strong> <code>%s</code></p>
                    </div>
                    <p>Please log in and change your password immediately.</p>
                  </div>
                </div>
                """.formatted(name, temporaryPassword);
        sendHtml(to, subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@smms.lk");
            helper.setText(html, true);
            mailSender.send(message);
            log.debug("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Non-fatal — log and continue; OTP is also shown in logs in dev mode
        }
    }
}
