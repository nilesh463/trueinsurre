package com.trueinsurre.user.serviceImpl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trueinsurre.modal.Otp;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.OtpRepository;
import com.trueinsurre.user.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;
    @Autowired
	JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long OTP_VALID_DURATION = 5; // in minutes
    
    public void generateOtp(String email) throws UnsupportedEncodingException, MessagingException {
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_VALID_DURATION);
        Optional<Otp> otpRecord = otpRepository.findByEmail(email);
        if (otpRecord.isPresent()) {
        	otpRecord.get().setOtp(otp);
        	otpRecord.get().setExpiryTime(expiryTime);
            otpRepository.save(otpRecord.get());
        }else {
        	System.out.println("");
        	Otp otpEntity = new Otp();
            otpEntity.setEmail(email);
            otpEntity.setOtp(otp);
            otpEntity.setExpiryTime(expiryTime);
            otpRepository.save(otpEntity);
        }
        System.out.println("Otp : "+otp);
        sendOtpEmail(email, otp);
    }
    
    public boolean validateUser(String email, String password) {
        User userRecord = userRepository.findByEmailAndStatus(email,false);
        if (userRecord!= null ) {
        	return passwordEncoder.matches(password, userRecord.getPassword());
        }
        return false;
    }

    public boolean validateOtp(String email, String otp) {
        Optional<Otp> otpRecord = otpRepository.findByEmailAndOtp(email, otp);
        if (otpRecord.isPresent() && otpRecord.get().getExpiryTime().isAfter(LocalDateTime.now())) {
            otpRepository.delete(otpRecord.get()); // OTP is single-use, delete it after validation
            return true;
        }
        return false;
    }

    public void sendOtpEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException {

    	System.out.println("message send!");
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		String subject = "Delete Account OTP Verification";
		
		String emailContent = "<center><p><img src='cid:invoicementLogo' height='100' width='275'></p></center>"
				+ "<center><h1>Your OTP for account deletion is:  "+ otp+"</h1></center>"
//				+ "<p>Your OTP for account deletion is:  "+ otp+"</p>"
				+ "<p><h3><b>Support and Assistance:</b></h3></p>"
				+ "<p>If you have any questions or need assistance at any point, our dedicated support team is here to help. Feel free to reach out to us via <a href='mailto:singhnilesh199@gmail.com'>support@interestbudsolutions.com</a> or call us at <a href='tel:+91 8076569119'>+91 8076569119</a> for prompt assistance.</p>"
				+ "<p><h3><b>Stay Connected:</b></h3></p>"
				+ "<p>Stay updated with the latest news, tips, and updates by following us on social media:</p>"
				+ "<p><img src='cid:linkedin'><a href='https://www.linkedin.com/company/interest-bud-solutions-pvt-ltd/mycompany/'>LinkedIn</a></p>"
				+ "<p><b>Warm regards,</b></p>" + "<p><b>The Invoicement Team</b></p>";

		helper.setText(emailContent, true);
		helper.setFrom("support@interestbudsolutions.com", "Invoicemint Support");
		helper.setSubject(subject);
		helper.setTo(email);

		FileSystemResource image = new FileSystemResource(new File("/opt/javaProjects/ibsInvoice/images/invoicement.png"));
//		FileSystemResource image = new FileSystemResource(new File("C:\\Users\\Ibs Nilesh\\nilesh_Workspace\\com.invoiceGenerator\\src\\main\\resources\\static\\images\\invoiceEmail.png"));
		
		helper.addInline("invoicementLogo", image, "image/png");

		FileSystemResource image1 = new FileSystemResource(new File("/opt/javaProjects/ibsInvoice/images/linkedin.png"));
//	    FileSystemResource image1 = new FileSystemResource(new File("C:\\Users\\Ibs Nilesh\\nilesh_Workspace\\com.invoiceGenerator\\src\\main\\resources\\static\\images\\invoicement.png"));
	    
		helper.addInline("linkedin", image1, "image/png");

		javaMailSender.send(message);
    }
}

