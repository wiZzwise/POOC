package org.proactive.mail;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.proactive.configuration.ProActiveProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Component
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private static final String BASE_URL = "baseUrl";

	private final ProActiveProperties proActiveProperties;

	private final JavaMailSender javaMailSender;

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	public MailService(ProActiveProperties proActiveProperties, JavaMailSender javaMailSender,
			MessageSource messageSource, SpringTemplateEngine templateEngine) {

		this.proActiveProperties = proActiveProperties;
		this.javaMailSender = javaMailSender;
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
	}

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
				isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom(proActiveProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.warn("Email could not be sent to user '{}'", to, e);
			} else {
				log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}

	// @Async
	// public void sendEmailFromTemplate(User user, String templateName, String
	// titleKey) {
	// Locale locale = Locale.forLanguageTag(user.getLangKey());
	// Context context = new Context(locale);
	// context.setVariable(USER, user);
	// context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
	// String content = templateEngine.process(templateName, context);
	// String subject = messageSource.getMessage(titleKey, null, locale);
	// sendEmail(user.getEmail(), subject, content, false, true);
	//
	// }

	@Async
	public void sendJobEmail(SimpleMailMessage simpleMessage) {
		log.debug("Sending job email to '{}'", simpleMessage.getTo());
		sendEmail(simpleMessage.getTo().toString(), simpleMessage.getSubject(), simpleMessage.getText(), false, true);

	}

}
