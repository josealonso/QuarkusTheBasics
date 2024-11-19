package org.acme.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {
    
    @Inject
    Mailer mailer;
    
    public void sendEmail(String to, String subject, String body) {
        mailer.send(Mail.withText(to, subject, body));
    }
    
    public void sendEmailAsync(String to, String subject, String body) {
        mailer.send(Mail.withText(to, subject, body));
    }
    
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        mailer.send(Mail.withHtml(to, subject, htmlContent));
    }
    
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String fileName) {
        mailer.send(
            Mail.withText(to, subject, body)
                .addAttachment(fileName, attachment, "application/pdf")
        );
    }
}
