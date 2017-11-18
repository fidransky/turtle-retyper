package cz.pavelfidransky.fav.dbm2;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import cz.pavelfidransky.fav.dbm2.worker.IRetypeWorker;

/**
 * Helper class for retyping input string to a different datatype.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class Retyper {

    public static Object retype(String string, RetypeStrategy strategy) throws Exception {
        switch (strategy) {
            case TO_INTEGER:
                return toInteger(string);

            case TO_FLOAT:
                return toFloat(string);

            case TO_DOUBLE:
                return toDouble(string);

            case TO_SQL_DATE:
                return toDate(string);

            case TO_SQL_TIME:
                return toTime(string);

            case TO_SQL_TIMESTAMP:
                return toTimestamp(string);

            case TO_DURATION:
                return toDuration(string);

            case TO_URL:
                return toURL(string);

            case TO_STRING:
                return string;

            default:
                throw new UnsupportedOperationException("Retyper does not support strategy " + strategy.name() + ".");
        }
    }

    private static Integer toInteger(String string) throws NumberFormatException {
        return Integer.parseInt(string);
    }

    private static Float toFloat(String string) throws NumberFormatException {
        return Float.parseFloat(string);
    }

    private static Double toDouble(String string) throws NumberFormatException {
        return Double.parseDouble(string);
    }

    private static Date toDate(String string) throws IllegalArgumentException {
        return Date.valueOf(string);
    }

    private static Time toTime(String string) throws IllegalArgumentException {
        return Time.valueOf(string);
    }

    private static Timestamp toTimestamp(String string) throws IllegalArgumentException {
        return Timestamp.valueOf(string);
    }

    private static Duration toDuration(String string) throws DateTimeParseException {
        return Duration.parse(string);
    }

    private static URL toURL(String string) throws MalformedURLException {
        return new URL(string);
    }

}
