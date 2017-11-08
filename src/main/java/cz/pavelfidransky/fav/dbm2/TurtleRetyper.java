package cz.pavelfidransky.fav.dbm2;

import java.util.Map;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;

/**
 * Accepts RDF model and returns the same model with values retyped to datatypes selected by user.
 * <p>
 * Date: 04.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class TurtleRetyper {

    /**
     * Model of input turtle statements.
     */
    protected Model inModel;
    /**
     * Chosen retyping strategies for each predicate.
     */
    protected Map<String, RetypeStrategy> strategies;

    /**
     * Default constructor accepting iterator of turtle statements as argument.
     * @param inModel input turtle statements model
     */
    public TurtleRetyper(Model inModel, Map<String, RetypeStrategy> strategies) {
        this.inModel = inModel;
        this.strategies = strategies;
    }


    public Model retype() {
        Model outModel = ModelFactory.createDefaultModel();

        StmtIterator iterator = inModel.listStatements();
        while (iterator.hasNext()) {
            Statement statement = iterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            // predicate
            String predicateFullName = predicate.getNameSpace() + predicate.getLocalName();
            if (!strategies.containsKey(predicateFullName)) {
                outModel.add(statement);
                continue;
            }

            RetypeStrategy strategy = strategies.get(predicateFullName);

            RDFNode retypedObject;
            if (!(object instanceof Literal)) {
                retypedObject = object;
            } else {
                try {
                    retypedObject = outModel.createTypedLiteral(Retyper.retype(object.asLiteral().getLexicalForm(), strategy));
                } catch (Exception e) {
                    System.out.println("Could not retype \"" + object.asLiteral().getLexicalForm() + "\" using " + strategy.getJavaClass().getName() + " due to following error:\n" + e.getCause());
                    retypedObject = object;
                }
            }

            outModel.add(new StatementImpl(subject, predicate, retypedObject));
        }

        return outModel;
    }

}
