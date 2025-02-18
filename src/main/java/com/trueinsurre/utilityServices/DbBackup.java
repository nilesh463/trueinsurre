package com.trueinsurre.utilityServices;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class DbBackup {
	
	@Autowired
	private JavaMailSender mailSender;

	private void sendBackupEmail(String filePath, String date) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo("bhattdaksh36@gmail.com"); // Change to recipient's email
		helper.setSubject("Daily Database Backup " + date);
		helper.setText("Please find the attached database backup.", true);
		helper.setFrom("support@interestbudsolutions.com", "From Trueinsurre Database Support");

		File backupFile = new File(filePath);
		helper.addAttachment(backupFile.getName(), backupFile);

		mailSender.send(message);
		System.out.println("Backup email sent successfully!");
	}

	@Scheduled(cron = "0 0 22 * * ?") // Runs daily at 10 PM
	public void exportDatabaseLinux() {
		try {
			// Get current date in yyyy-MM-dd format
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			System.out.println("Timestamp: " + timestamp);

			// Backup file path
			String backupDir = "/opt/javaProjects/ibsInvoice/dbbackup/";
			String backupFilePath = backupDir + "Trueinsurre" + timestamp + ".sql";

			// Ensure backup directory exists
			new File(backupDir).mkdirs();

			// Construct the mysqldump command
			String command = "mysqldump -u root --password=mukul@Ibs2025# trueinsurre > " + backupFilePath + " 2>&1";

			// Execute the command
			Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
			int exitCode = process.waitFor();

			if (exitCode == 0) {
				System.out.println("Database backup completed: " + backupFilePath);
				sendBackupEmail(backupFilePath, timestamp);
			} else {
				System.err.println("Database backup failed. Check for errors.");
			}

		} catch (IOException | InterruptedException | MessagingException e) {
			e.printStackTrace();
		}
	}


}
