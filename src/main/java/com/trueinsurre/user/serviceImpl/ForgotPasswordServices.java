package com.trueinsurre.user.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.trueinsurre.modal.ForgotPassword;
import com.trueinsurre.modal.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ForgotPasswordServices {
	
	@Autowired
	JavaMailSender javaMailSender;
	
	private final int MINUTES = 10;
	
	public String generateToken() {
		return UUID.randomUUID().toString();
	}
	
	public LocalDateTime expireTimeRange() {
		return LocalDateTime.now().plusMinutes(MINUTES);
	}

	public void sendEmail(String to, String subject, String emailLink, User user) throws MessagingException, UnsupportedEncodingException {
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		String emailContent = "<p> Hello "+user.getName()+"</p>"
							+"Click the link below to reset password."
				+"<p><a href=\""+emailLink+"\">Change my password</a></p>"
				+"<br>"
				+"<b>Ignore this email if you do not made the request.</b>";
		
		helper.setText(emailContent,true);
		helper.setFrom("support@interestbudsolutions.com","Invoicemint Support");
		helper.setSubject(subject);
		helper.setTo(to);
		javaMailSender.send(message);
	}
	
	public boolean isExpired(ForgotPassword forgotPassword) {
		boolean isExpired = LocalDateTime.now().isAfter(forgotPassword.getExpairTime());
		return isExpired;
	}
	
	public String checkValidity(ForgotPassword forgotPassword, Model model) {
		if(forgotPassword == null) {
			model.addAttribute("error","Invalid token");
			return "passwordReset/errorPassword";
		}else if(forgotPassword.isUsed()) {
			model.addAttribute("error","The link is already used.");
			return "passwordReset/errorPassword";
		}else if(isExpired(forgotPassword)) {
			model.addAttribute("error","The link is expired.");
			return "passwordReset/errorPassword";
		}else {
			return "passwordReset/reset-password";
		}
		
	}
	
}
