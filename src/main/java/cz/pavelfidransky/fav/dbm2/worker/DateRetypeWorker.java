package cz.pavelfidransky.fav.dbm2.worker;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;

/**
 * Custom retype worker for parsing date and time in given format (e.g. 2017/1/5 07:06) to java Date object.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class DateRetypeWorker implements IRetypeWorker {

    @Override
    public void retype(Model outModel, Statement inStatement) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy/M/d HH:mm");

        Date retypedObject = format.parse(inStatement.getObject().asLiteral().getLexicalForm());

        Statement retypedStatement = new StatementImpl(inStatement.getSubject(), inStatement.getPredicate(), outModel.createTypedLiteral(retypedObject));
        outModel.add(retypedStatement);
    }

}
