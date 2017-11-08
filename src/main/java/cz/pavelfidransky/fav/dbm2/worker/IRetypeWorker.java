package cz.pavelfidransky.fav.dbm2.worker;

/**
 * Interface that must be implemented by custom retype workers.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public interface IRetypeWorker<T> {

    T parse(String string) throws Exception;

}
