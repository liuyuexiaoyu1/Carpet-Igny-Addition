package com.liuyue.igny.utils;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.time.LocalDate;
import java.time.ZoneId;

public class FestivalUtil {
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    private static volatile LocalDate midAutumnDate;
    private static volatile boolean midAutumn;

    public static boolean isAprilFoolsDay() {
        LocalDate now = LocalDate.now();
        return now.getMonthValue() == 4 && now.getDayOfMonth() == 1;
    }

    public static boolean isAuthorsBirthday() {
        LocalDate now = LocalDate.now(BEIJING_ZONE);
        return now.getMonthValue() == 11 && now.getDayOfMonth() == 2;
    }

    public static boolean isMidAutumnDay() {
        LocalDate today = LocalDate.now(BEIJING_ZONE);

        if (today.equals(midAutumnDate)) {
            return midAutumn;
        }

        Lunar lunar = Solar.fromYmd(today.getYear(), today.getMonthValue(), today.getDayOfMonth()).getLunar();
        boolean value = lunar.getMonth() == 8 && lunar.getDay() == 15;
        midAutumn = value;
        midAutumnDate = today;
        return value;
    }
}
