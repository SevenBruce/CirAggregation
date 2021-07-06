package edu.bjut;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    static String pattern = "yyyyMMdd'T'HHmmss";

    public static String Now() {
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String nowAsString = df.format(today);
        return nowAsString;
    }
    
}
