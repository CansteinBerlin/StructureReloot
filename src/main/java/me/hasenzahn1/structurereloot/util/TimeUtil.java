package me.hasenzahn1.structurereloot.util;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Pattern periodPattern = Pattern.compile("([0-9]+)([smhdMWwYy])");

    public static Long parsePeriodToSeconds(String period) {
        if (period == null) return null;
        Matcher matcher = periodPattern.matcher(period);
        Instant instant = Instant.EPOCH;
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch (typ) {
                case "s" -> instant = instant.plus(Duration.ofSeconds(num));
                case "m" -> instant = instant.plus(Duration.ofMinutes(num));
                case "h" -> instant = instant.plus(Duration.ofHours(num));
                case "d" -> instant = instant.plus(Duration.ofDays(num));
                case "W", "w" -> instant = instant.plus(Duration.ofDays(num * 7L));
                case "M" -> instant = instant.plus(Duration.ofDays(num * 30L));
                case "Y", "y" -> instant = instant.plus(Duration.ofDays(num * 365L));
            }
        }
        return instant.getEpochSecond();
    }
}
