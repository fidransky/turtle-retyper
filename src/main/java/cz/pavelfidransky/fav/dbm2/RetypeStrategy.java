package cz.pavelfidransky.fav.dbm2;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import cz.pavelfidransky.fav.dbm2.worker.DateRetypeWorker;
import cz.pavelfidransky.fav.dbm2.worker.RegionRetypeWorker;

/**
 * Enumerate holding all supported retyping strategies, including custom retype workers.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public enum RetypeStrategy {

    TO_INTEGER(Integer.class),
    TO_FLOAT(Float.class),
    TO_DOUBLE(Double.class),
    TO_SQL_DATE(Date.class),
    TO_SQL_TIME(Time.class),
    TO_SQL_TIMESTAMP(Timestamp.class),
    TO_DURATION(Duration.class),
    TO_URL(URL.class),
    TO_STRING(String.class),

    // custom workers
    TO_DATE(DateRetypeWorker.class),
    TO_REGION(RegionRetypeWorker.class),
    ;

    private Class javaClass;

    RetypeStrategy(Class javaClass) {
        this.javaClass = javaClass;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public static Optional<RetypeStrategy> from(String javaClassName) {
        return Arrays.stream(values()).filter(strategy -> strategy.getJavaClass().getName().equals(javaClassName)).findFirst();
    }

}
