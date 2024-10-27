package wtf.n1zamu.presents.util;

import wtf.n1zamu.presents.NPresents;

import java.util.concurrent.TimeUnit;

public class TimeFormatUtil {
    public static String getFormattedCooldown(long millis) {
        if (millis <= 60000L) {
            return getFormattedCooldownSeconds(millis);
        }
        if (millis <= 3600000L) {
            return getFormattedCooldownMinutes(millis);
        }
        if (millis <= 86400000L) {
            return getFormattedCooldownHours(millis);
        }
        return getFormattedCooldownDays(millis);
    }

    private static String getFormattedCooldownDays(final long millis) {
        return String.format("%d " + NPresents.getInstance().getConfig().getString("time.days")
                + " %d " + NPresents.getInstance().getConfig().getString("time.hours")
                + " %d " + NPresents.getInstance().getConfig().getString("time.minutes")
                + " %d " + NPresents.getInstance().getConfig().getString("time.seconds"), TimeUnit.MILLISECONDS.toDays(millis), TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static String getFormattedCooldownHours(final long millis) {
        return String.format("%d " + NPresents.getInstance().getConfig().getString("time.hours")
                + " %d " + NPresents.getInstance().getConfig().getString("time.minutes")
                + " %d " + NPresents.getInstance().getConfig().getString("time.seconds"), TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static String getFormattedCooldownMinutes(final long millis) {
        return String.format("%d " + NPresents.getInstance().getConfig().getString("time.minutes")
                + " %d " + NPresents.getInstance().getConfig().getString("time.seconds"),
                TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static String getFormattedCooldownSeconds(final long millis) {
        return millis / 1000L + " " + NPresents.getInstance().getConfig().getString("time.seconds");
    }
}

