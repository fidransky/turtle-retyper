package cz.pavelfidransky.fav.dbm2.worker;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Custom retype worker for parsing region string to enumerate.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 * @see cz.pavelfidransky.fav.dbm2.datatype.Region
 */
public class RegionRetypeWorker implements IRetypeWorker {

    @Override
    public void retype(Model outModel, Statement inStatement) throws IllegalArgumentException {
        String object = inStatement.getObject().asLiteral().getLexicalForm();

        StringBuilder sb = new StringBuilder();
        sb.append(outModel.getNsPrefixURI("id"));
        sb.append(inStatement.getPredicate().getLocalName());
        sb.append("_");
        sb.append(object);

        Resource subject = inStatement.getSubject();
        Property predicate = outModel.getProperty(sb.toString());

        outModel.add(subject, predicate, object);
    }

}
