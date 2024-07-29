package com.sahajamit.utils;

import net.datafaker.Faker;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateUtils {
    public static String convertDateToString(Date date, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getFakePostingData(){
        Faker faker = FakerUtil.getInstance();
        Timestamp randomTimestamp = new Timestamp(faker.date().birthday().getTime());
        LocalDateTime localDateTime = randomTimestamp.toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDate.format(formatter);
        return formattedDate;
    }
}
