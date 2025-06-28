package com.trueinsurre.utilityServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateUtility {

	public static String convertToMMDDYYYY(String inputDate) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
		if (inputDate != null && inputDate != "") {
			try {
				Date date = inputFormat.parse(inputDate);
				return outputFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
				return null; // or return "Invalid date"
			}
		} else {
			return "";
		}
	}
	

}
