package com.trueinsurre.utilityServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateUtility {
	
	// Convert dd-MM-yyyy to MM/dd/yyyy
    public static String convertToMMDDYYYY(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date parsedDate = inputFormat.parse(date);
        return outputFormat.format(parsedDate);
    }

    // Convert MM/dd/yyyy to dd-MM-yyyy
    public static String convertToDDMMYYYY(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date parsedDate = inputFormat.parse(date);
        return outputFormat.format(parsedDate);
    }

}
