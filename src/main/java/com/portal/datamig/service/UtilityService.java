package com.portal.datamig.service;

import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Service
public class UtilityService {

    public String greet() {
        String greeting = null;
        GregorianCalendar time = new GregorianCalendar();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        if (hour < 12)
            greeting = "Good Morning!";
        else if (hour < 17 && !(hour == 12))
            greeting = "Good Afternoon";
        else if (hour == 12)
            greeting = "Good Noon";
        else
            greeting = "Good Evening";

        return greeting;
    }
}
