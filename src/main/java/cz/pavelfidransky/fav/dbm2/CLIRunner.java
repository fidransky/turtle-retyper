package cz.pavelfidransky.fav.dbm2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.jena.n3.turtle.TurtleParseException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.LiteralRequiredException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * Main class of Turtle Retyper running in command line.
 * <p>
 * Date: 08.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class CLIRunner {

    private File file;

    /**
     * @param fileName name of the input file
     */
    public CLIRunner(String fileName) {
        this.file = new File(Main.BASE_PATH + fileName);
    }


    public void run() {
        // print copyright info
        String horizontalLine = String.join("", Collections.nCopies(80, "="));

        System.out.println(horizontalLine);
        System.out.println("Turtle Retyper\n");
        System.out.println("author: \tPavel Fidransky (http://pavelfidransky.cz)");
        System.out.println("created:\t2017");
        System.out.println("license:\tCC BY-NC-SA 4.0 (https://creativecommons.org/licenses/by-nc-sa/4.0/)");
        System.out.println(horizontalLine);

        // work
        Model inModel = openTurtleFile(file);

        Map<String, Integer> dataTypes = collectDataTypes(inModel);
        printStatistics(dataTypes);

        Map<String, RDFNode> predicates = CommonUtils.collectPredicates(inModel);
        Map<String, RetypeStrategy> strategies = resolveRetypeStrategies(predicates);

        TurtleRetyper retyper = new TurtleRetyper(inModel, strategies);
        Model outModel = retyper.retype();
        outModel.setNsPrefixes(inModel.getNsPrefixMap());

        saveTurtleFile(outModel);

        System.exit(0);
    }

    private Model openTurtleFile(File file) {
        TurtleParser parser = new TurtleParser(file);
        Model inModel = null;
        try {
            inModel = parser.parse();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(2);

        } catch (TurtleParseException e) {
            System.out.println("File is not in valid turtle format.");
            System.exit(3);
        }

        return inModel;
    }

    private void saveTurtleFile(Model outModel) {
        File outputFile = new File(file.getParent() + "/retyped-" + file.getName());

        try {
            OutputStream os = new FileOutputStream(outputFile);
            RDFDataMgr.write(os, outModel, RDFFormat.TURTLE_PRETTY);
            os.close();

        } catch (FileNotFoundException e) {
            System.out.println("Cannot open output file.");
            System.exit(4);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(5);
        }

        System.out.println("File was saved to " + outputFile.getPath() + ".");
    }

    private Map<String, Integer> collectDataTypes(Model inModel) {
        Map<String, Integer> dataTypes = new HashMap<>();

        StmtIterator iterator = inModel.listStatements();
        while (iterator.hasNext()) {
            Statement statement = iterator.next();

            RDFNode object = statement.getObject();

            // data type
            Literal literal;
            try {
                literal = object.asLiteral();

            } catch (LiteralRequiredException e) {
                continue;
            }

            String dataType = literal.getDatatype().getJavaClass().getName();
            if (dataTypes.containsKey(dataType)) {
                dataTypes.replace(dataType, dataTypes.get(dataType) + 1);
            } else {
                dataTypes.put(dataType, 1);
            }
        }

        return dataTypes;
    }

    private void printStatistics(Map<String, Integer> dataTypes) {
        System.out.println("Statistics:\n");

        int total = dataTypes.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : dataTypes.entrySet()) {
            System.out.println(entry.getKey());

            float percentage = entry.getValue() * 100F / total;

            System.out.print("|");
            for (int i = 0; i < Math.round(percentage); i++) {
                System.out.print("=");
            }

            for (int i = Math.round(percentage); i < 100; i++) {
                System.out.print(" ");
            }

            System.out.print("| " + String.format("%.2f", percentage) + "%\n");
        }

        System.out.print("\n");

        // wait for enter
        System.out.println("Press enter to continue...");
        Scanner in = new Scanner(System.in);
        in.nextLine();
    }

    private Map<String, RetypeStrategy> resolveRetypeStrategies(Map<String, RDFNode> predicates) {
        Map<String, RetypeStrategy> strategies = new HashMap<>();

        for (Map.Entry<String, RDFNode> entry : predicates.entrySet()) {
            RDFNode object = entry.getValue();

            if (!(object instanceof Literal)) {
                continue;
            }

            System.out.println("predicate: " + entry.getKey());
            System.out.println("sample value: " + object.asLiteral().getLexicalForm());

            RetypeStrategy estimatedStrategy = CommonUtils.estimateRetypeStrategy(object.asLiteral());
            RetypeStrategy chosenStrategy = askForRetypeStrategy(estimatedStrategy);

            strategies.put(entry.getKey(), chosenStrategy);

            System.out.println("\n\n");
        }

        return strategies;
    }

    private RetypeStrategy askForRetypeStrategy(RetypeStrategy estimatedStrategy) {
        // print retype strategy options
        System.out.println("retyping strategies:");
        for (RetypeStrategy strategy : RetypeStrategy.values()) {
            System.out.println("\t" + "(" + (strategy.ordinal() + 1) + ") " + strategy.getJavaClass().getName());
        }

        int chosenStrategyNumber = estimatedStrategy.ordinal();
        System.out.print("choose retyping strategy (" + (chosenStrategyNumber + 1) + "): ");

        // get retype strategy number
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        if (!line.trim().equals("")) {
            try {
                chosenStrategyNumber = Integer.parseInt(line) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again");
                return askForRetypeStrategy(estimatedStrategy);
            }
        }

        try {
            return RetypeStrategy.values()[chosenStrategyNumber];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number, try again");
            return askForRetypeStrategy(estimatedStrategy);
        }
    }

}
