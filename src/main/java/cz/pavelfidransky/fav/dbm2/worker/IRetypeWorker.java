package cz.pavelfidransky.fav.dbm2.worker;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

/**
 * Interface that must be implemented by custom retype workers. The workers are responsible for parsing the statement
 * and storing the result in output model.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public interface IRetypeWorker {

    void retype(Model outModel, Statement inStatement) throws Exception;

}
