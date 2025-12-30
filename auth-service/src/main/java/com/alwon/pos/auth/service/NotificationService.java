package com.alwon.pos.auth.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending notifications via WhatsApp (Twilio) and Email (SendGrid)
 */
@Slf4j
@Service
public class NotificationService {

    @Value("${twilio.account.sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token:}")
    private String twilioAuthToken;

    @Value("${twilio.whatsapp.from:}")
    private String twilioWhatsAppFrom;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${sendgrid.api.key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email:}")
    private String sendGridFromEmail;

    @Value("${sendgrid.from.name:Alwon POS}")
    private String sendGridFromName;

    @Value("${sendgrid.enabled:false}")
    private boolean sendGridEnabled;

    @PostConstruct
    public void init() {
        if (twilioEnabled && twilioAccountSid != null && !twilioAccountSid.isEmpty()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio is DISABLED or not configured");
        }

        if (sendGridEnabled && sendGridApiKey != null && !sendGridApiKey.isEmpty()) {
            log.info("SendGrid configured successfully");
        } else {
            log.warn("SendGrid is DISABLED or not configured");
        }
    }

    /**
     * Send PIN via WhatsApp (async)
     */
    @Async
    public CompletableFuture<Boolean> sendPinViaWhatsApp(String phoneNumber, String fullName, String pin) {
        if (!twilioEnabled) {
            log.warn("Twilio is disabled - skipping WhatsApp notification");
            return CompletableFuture.completedFuture(false);
        }

        try {
            String messageBody = String.format(
                    "🔐 *Alwon POS*\n\n" +
                            "Hola %s,\n\n" +
                            "Tu PIN temporal es: *%s*\n\n" +
                            "Este PIN es válido por 8 horas.\n\n" +
                            "No compartas este código con nadie.",
                    fullName, pin);

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + phoneNumber),
                    new PhoneNumber(twilioWhatsAppFrom),
                    messageBody).create();

            log.info("WhatsApp sent successfully to {} - SID: {}", maskPhone(phoneNumber), message.getSid());
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            log.error("Error sending WhatsApp to {}: {}", maskPhone(phoneNumber), e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Send PIN via Email (async)
     */
    @Async
    public CompletableFuture<Boolean> sendPinViaEmail(String email, String fullName, String pin) {
        if (!sendGridEnabled) {
            log.warn("SendGrid is disabled - skipping email notification");
            return CompletableFuture.completedFuture(false);
        }

        try {
            Email from = new Email(sendGridFromEmail, sendGridFromName);
            Email to = new Email(email);
            String subject = "Tu PIN temporal - Alwon POS";

            String htmlContent = buildEmailHtml(fullName, pin);
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully to {} - Status: {}", maskEmail(email), response.getStatusCode());
                return CompletableFuture.completedFuture(true);
            } else {
                log.error("Email failed to {} - Status: {}", maskEmail(email), response.getStatusCode());
                return CompletableFuture.completedFuture(false);
            }

        } catch (IOException e) {
            log.error("Error sending email to {}: {}", maskEmail(email), e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Build HTML email template
     */
    private String buildEmailHtml(String fullName, String pin) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #f4f4f4;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background: white;
                            padding: 40px;
                            border-radius: 10px;
                        }
                        .header {
                            text-align: center;
                            color: #667eea;
                            font-size: 28px;
                            margin-bottom: 20px;
                        }
                        .pin {
                            font-size: 48px;
                            letter-spacing: 10px;
                            text-align: center;
                            color: #667eea;
                            font-weight: bold;
                            margin: 30px 0;
                            padding: 20px;
                            background: #f0f4ff;
                            border-radius: 10px;
                        }
                        .info {
                            color: #636e72;
                            line-height: 1.6;
                            text-align: center;
                        }
                        .footer {
                            margin-top: 30px;
                            text-align: center;
                            color: #999;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1 class="header">🔐 Alwon POS</h1>
                        <p class="info">Hola <strong>%s</strong>,</p>
                        <p class="info">Tu PIN temporal es:</p>
                        <div class="pin">%s</div>
                        <p class="info">Este PIN es válido por <strong>8 horas</strong>.</p>
                        <p class="info">No compartas este código con nadie.</p>
                        <div class="footer">
                            <p>Este es un mensaje automático de Alwon POS.</p>
                            <p>Si no solicitaste este PIN, ignora este mensaje.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, fullName, pin);
    }

    /**
     * Mask phone number for privacy (***-***-4567)
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        String lastFour = phone.substring(phone.length() - 4);
        return "***-***-" + lastFour;
    }

    /**
     * Mask email for privacy (c***@alwon.com)
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.***";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        String maskedUsername = username.length() > 1
                ? username.charAt(0) + "***"
                : "***";

        return maskedUsername + "@" + domain;
    }
}
