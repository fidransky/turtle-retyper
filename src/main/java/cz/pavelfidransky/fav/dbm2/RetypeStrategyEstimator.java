package cz.pavelfidransky.fav.dbm2;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import cz.pavelfidransky.fav.dbm2.RetypeStrategy;

/**
 * Helper class that tries to estimate what is the best target datatype for the input string.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class RetypeStrategyEstimator {

    public static RetypeStrategy estimate(String value) {
        if (isInteger(value)) {
            return RetypeStrategy.TO_INTEGER;

        } else if (isFloat(value)) {
            return RetypeStrategy.TO_FLOAT;

        } else if (isDouble(value)) {
            return RetypeStrategy.TO_DOUBLE;

        } else if (isDate(value)) {
            return RetypeStrategy.TO_SQL_DATE;

        } else if (isTime(value)) {
            return RetypeStrategy.TO_SQL_TIME;

        } else if (isTimestamp(value)) {
            return RetypeStrategy.TO_SQL_TIMESTAMP;

        } else if (isDuration(value)) {
            return RetypeStrategy.TO_DURATION;

        } else if (isURL(value)) {
            return RetypeStrategy.TO_URL;
        }

        return RetypeStrategy.TO_STRING;
    }

    private static boolean isInteger(String string) {
        try {
            new BigInteger(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isDate(String string) {
        try {
            Date.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private static boolean isTime(String string) {
        try {
            Time.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private static boolean isTimestamp(String string) {
        try {
            Timestamp.valueOf(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private static boolean isDuration(String string) {
        try {
            Duration.parse(string);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private static boolean isURL(String string) {
        try {
            new URL(string);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

}
