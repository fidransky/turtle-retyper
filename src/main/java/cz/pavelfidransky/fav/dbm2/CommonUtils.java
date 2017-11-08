package cz.pavelfidransky.fav.dbm2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Set of static methods common for CLI and GUI.
 * <p>
 * Date: 08.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class CommonUtils {

    public static Map<String, RDFNode> collectPredicates(Model inModel) {
        Map<String, RDFNode> predicates = new HashMap<>();

        StmtIterator iterator = inModel.listStatements();
        while (iterator.hasNext()) {
            Statement statement = iterator.next();

            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            // predicate
            String predicateFullName = predicate.getNameSpace() + predicate.getLocalName();
            if (!predicates.containsKey(predicateFullName)) {
                predicates.put(predicateFullName, object);
            }
        }

        return predicates;
    }

    public static RetypeStrategy estimateRetypeStrategy(Literal literal) {
        String dataType = literal.getDatatype().getJavaClass().getName();
        Optional<RetypeStrategy> strategy = RetypeStrategy.from(dataType);

        if (strategy.isPresent() && !strategy.get().equals(RetypeStrategy.TO_STRING)) {
            return strategy.get();
        }

        // estimate best retype strategy
        return RetypeStrategyEstimator.estimate(literal.getLexicalForm());
    }

}
