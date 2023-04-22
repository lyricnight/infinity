package me.lyric.infinity.api.util.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lyric
 */

public class DateTimeUtils {

    public static final String TIME_AND_DATE = "uuuu-MM-dd-HH-mm-ss";

    public static String time(String pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        final LocalDateTime time = LocalDateTime.now();

        return formatter.format(time);
    }
}
