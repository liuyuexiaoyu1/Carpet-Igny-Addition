package com.liuyue.igny.utils;

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
}
