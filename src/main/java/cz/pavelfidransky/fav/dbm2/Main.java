package cz.pavelfidransky.fav.dbm2;

import javax.swing.*;

/**
 * Turtle Retyper bootstrap class.
 * <p>
 * Date: 10.10.2017
 *
 * @author Pavel Fidransky [jsem@pavelfidransky.cz]
 */
public class Main {

    public static String BASE_PATH = "./";


    public static void main(String... argv) {
        // no arguments passed, start GUI
        if (argv.length == 0) {
            SwingUtilities.invokeLater(() -> {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                startGUI();
            });

        // otherwise start CLI
        } else {
            startCLI(argv[0]);
        }
    }

    private static void startGUI() {
        JFrame window = new GUIWindow();
        window.setVisible(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(null);
    }

    private static void startCLI(String fileName) {
        CLIRunner cliRunner = new CLIRunner(fileName);
        cliRunner.run();
    }

}
