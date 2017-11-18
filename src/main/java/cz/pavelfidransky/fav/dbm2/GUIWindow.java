package cz.pavelfidransky.fav.dbm2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;

import org.apache.jena.n3.turtle.TurtleParseException;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

/**
 * Main class of Turtle Retyper running in graphical interface.
 * <p>
 * Date: 08.11.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class GUIWindow extends JFrame {

    private JPanel radioPanel;

    public GUIWindow() throws HeadlessException {
        super("Turtle Retyper");

        addComponents();
    }

    private void addComponents() {
        Container container = getContentPane();
        container.setLayout(new GridBagLayout());
        container.setPreferredSize(new Dimension(350, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // open file button
        JFileChooser fileChooser = new JFileChooser(Main.BASE_PATH);

        JButton openButton = new JButton("Open a turtle file...");
        openButton.addActionListener((ActionEvent e) -> {
            int returnVal = fileChooser.showOpenDialog(GUIWindow.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                openFileCallback(fileChooser.getSelectedFile());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(openButton, gbc);

        // radio list panel
        radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        container.add(radioPanel, gbc);

        // copyright info
        JLabel copyrightLabel = new JLabel("© 2017 Pavel Fidransky, CC BY-NC-SA 4.0");
        copyrightLabel.setFont(copyrightLabel.getFont().deriveFont(10F));

        gbc.gridx = 0;
        gbc.gridy = 2;
        container.add(copyrightLabel, gbc);
    }

    private void openFileCallback(File file) {
        Model inModel = openTurtleFile(file);

        Map<String, RDFNode> predicates = CommonUtils.collectPredicates(inModel);
        resolveRetypeStrategies(inModel, predicates);
    }

    private void resolveStrategiesCallback(Model inModel, Map<String, RetypeStrategy> strategies) {
        radioPanel.setVisible(false);
        radioPanel.revalidate();
        radioPanel.repaint();

        TurtleRetyper retyper = new TurtleRetyper(inModel, strategies);
        Model outModel = retyper.retype();

        saveTurtleFile(outModel);
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
        JFileChooser fileChooser = new JFileChooser(Main.BASE_PATH);

        int returnVal = fileChooser.showSaveDialog(GUIWindow.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                OutputStream os = new FileOutputStream(fileChooser.getSelectedFile());
                RDFDataMgr.write(os, outModel, RDFFormat.TURTLE_PRETTY);
                os.close();

            } catch (FileNotFoundException e) {
                System.out.println("Cannot open output file.");
                System.exit(4);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(5);
            }
        }
    }

    private void resolveRetypeStrategies(Model inModel, Map<String, RDFNode> predicates) {
        Iterator<Map.Entry<String, RDFNode>> iterator = predicates.entrySet().iterator();
        Map<String, RetypeStrategy> strategies = new HashMap<>();

        askForRetypeStrategy(inModel, iterator, strategies);
    }

    private void askForRetypeStrategy(Model inModel, Iterator<Map.Entry<String, RDFNode>> iterator, Map<String, RetypeStrategy> strategies) {
        Map.Entry<String, RDFNode> entry = iterator.next();

        String predicateName = entry.getKey();
        RDFNode object = entry.getValue();

        if (!(object instanceof Literal)) {
            return;
        }

        radioPanel.removeAll();
        radioPanel.setVisible(true);

        radioPanel.add(new JLabel("predicate: " + predicateName));
        radioPanel.add(new JLabel("sample value: " + object.asLiteral().getLexicalForm()));

        RetypeStrategy estimatedStrategy = CommonUtils.estimateRetypeStrategy(object.asLiteral());

        // radio button list
        ButtonGroup radioButtonGroup = new ButtonGroup();

        for (RetypeStrategy strategy : RetypeStrategy.values()) {
            JRadioButton radioButton = new JRadioButton(strategy.getJavaClass().getName());
            radioButton.setActionCommand(strategy.getJavaClass().getName());
            radioButton.setSelected(strategy.equals(estimatedStrategy));

            radioButtonGroup.add(radioButton);
            radioPanel.add(radioButton);
        }

        // next button
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            RetypeStrategy strategy = RetypeStrategy.from(radioButtonGroup.getSelection().getActionCommand()).orElseThrow(() -> new RuntimeException("Strategy \""+ e.getActionCommand() +"\" was not found."));

            strategies.put(predicateName, strategy);

            if (iterator.hasNext()) {
                askForRetypeStrategy(inModel, iterator, strategies);
            } else {
                resolveStrategiesCallback(inModel, strategies);
            }
        });
        radioPanel.add(nextButton);

        radioPanel.revalidate();
        radioPanel.repaint();
    }

}
