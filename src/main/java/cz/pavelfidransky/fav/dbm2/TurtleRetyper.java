package cz.pavelfidransky.fav.dbm2;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;

import cz.pavelfidransky.fav.dbm2.worker.IRetypeWorker;

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
     * Model of output turtle statements.
     */
    protected Model outModel;

    /**
     * Default constructor accepting iterator of turtle statements as argument.
     * @param inModel input turtle statements model
     */
    public TurtleRetyper(Model inModel, Map<String, RetypeStrategy> strategies) {
        this.inModel = inModel;
        this.strategies = strategies;

        this.outModel = ModelFactory.createDefaultModel();
        this.outModel.setNsPrefixes(inModel.getNsPrefixMap());
    }


    public Model retype() {
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

            if (!(object instanceof Literal)) {
                outModel.add(statement);
            } else {
                try {
                    Object retypedObject = Retyper.retype(object.asLiteral().getLexicalForm(), strategy);

                    Statement retypedStatement = new StatementImpl(subject, predicate, outModel.createTypedLiteral(retypedObject));
                    outModel.add(retypedStatement);

                } catch (UnsupportedOperationException e) {
                    retypeUsingCustomStrategy(statement, strategy);

                } catch (Exception e) {
                    System.out.println("Could not retype \"" + object.asLiteral().getLexicalForm() + "\" using " + strategy.getJavaClass().getName() + " due to following error:\n" + e.getCause());
                    outModel.add(statement);
                }
            }
        }

        return outModel;
    }

    private void retypeUsingCustomStrategy(Statement statement, RetypeStrategy strategy) {
        Object retypeWorker;
        try {
            retypeWorker = strategy.getJavaClass().newInstance();

        } catch (InstantiationException|IllegalAccessException e) {
            outModel.add(statement);
            return;
        }

        if (!(retypeWorker instanceof IRetypeWorker)) {
            throw new UnsupportedOperationException(strategy.getJavaClass() + " does not implement " + IRetypeWorker.class.getName() + " interface.");
        }

        try {
            strategy.getJavaClass().getMethod("retype", Model.class, Statement.class).invoke(retypeWorker, outModel, statement);

        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            throw new UnsupportedOperationException(strategy.getJavaClass() + " does not implement " + IRetypeWorker.class.getName() + " interface correctly.");

        } catch (Exception e) {
            System.out.println("Could not retype " + statement.toString() + " using " + strategy.getJavaClass().getName() + " due to following error:\n" + e.getCause());
            outModel.add(statement);
        }
    }

}
