package com.liuyue.igny.utils;

import com.nlf.calendar.Lunar;

import java.time.LocalDate;
import java.time.ZoneId;

public class FestivalUtil {
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    public static boolean isAprilFoolsDay() {
        LocalDate now = LocalDate.now();
        return now.getMonthValue() == 4 && now.getDayOfMonth() == 1;
    }

    public static boolean isAuthorsBirthday() {
        LocalDate now = LocalDate.now(BEIJING_ZONE);
        return now.getMonthValue() == 11 && now.getDayOfMonth() == 2;
    }

    public static boolean isMidAutumnDay() {
        Lunar now = new Lunar();
        return now.getMonth() == 8 && now.getDay() == 15;
    }
}
