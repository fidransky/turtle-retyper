package cz.pavelfidransky.fav.dbm2;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.jena.n3.turtle.TurtleParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;

/**
 * Parses turtle file to RDF model. If the file does not exist or is not valid turtle file, exception is thrown.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class TurtleParser {

    /**
     * Path to the file to be parsed.
     */
    private File file;

    /**
     * Default constructor accepting file instance as argument.
     * @param file the file to be parsed
     */
    public TurtleParser(File file) {
        this.file = file;
    }

    /**
     * Parses turtle file to RDF model.
     * @throws FileNotFoundException thrown when file does not exist
     * @throws TurtleParseException thrown when file cannot be parsed as turtle format
     */
    public Model parse() throws FileNotFoundException, TurtleParseException {
        if (!file.isFile()) {
            throw new FileNotFoundException("File does not exist.");
        }

        Model model = ModelFactory.createDefaultModel();

        try {
            model.read(file.getPath());
        } catch (RiotException e) {
            throw new TurtleParseException(e.getMessage());
        }

        return model;
    }

}
