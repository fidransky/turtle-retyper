package cz.pavelfidransky.fav.dbm2.worker;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Custom retype worker for parsing date and time in given format (e.g. 2017/1/5 07:06) to java Date object.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class DateRetypeWorker implements IRetypeWorker<Date> {

    @Override
    public Date parse(String string) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy/M/d HH:mm");

        return format.parse(string);
    }

}
