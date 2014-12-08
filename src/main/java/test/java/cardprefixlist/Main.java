package test.java.cardprefixlist;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * @author Dimitrijs Fedotovs
 */
public class Main {

    public static final char OPT_IGNORE_PARSING_ERRORS = 'e';
    public static final char OPT_VERBOSE_OUTPUT = 'v';
    public static final char OPT_CONFIG_FILE = 'c';
    public static final char OPT_CARD_NUMBER = 'n';
    public static final char OPT_HELP = 'h';
    public static final char OPT_QUIET = 'q';
    private final CardList cardList = new CardList();
    private final Parser parser = new Parser();
    private boolean verbose;
    private boolean ignoreParsingErrors;
    private boolean quiet;
    private String configFile;
    private Supplier<String> cardNumbersSupplier;

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new PosixParser();
        boolean verbose = true;
        try {
            CommandLine cmd = parser.parse(options, args);
            verbose = cmd.hasOption(OPT_VERBOSE_OUTPUT);
            if (cmd.hasOption(OPT_HELP)) {
                printHelp(options);
            } else {
                Main main = new Main();
                main.verbose = verbose;
                main.ignoreParsingErrors = cmd.hasOption(OPT_IGNORE_PARSING_ERRORS);
                main.quiet = cmd.hasOption(OPT_QUIET);
                main.configFile = cmd.getOptionValue(OPT_CONFIG_FILE);
                String[] cardNumbers = cmd.getOptionValues(OPT_CARD_NUMBER);

                main.cardNumbersSupplier = new Supplier<String>() {
                    @Override
                    public String get() {
                        return null;
                    }
                }
                main.processCards();
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
        } catch (Exception e) {
            if (verbose) {
                System.err.println("Fatal error:");
                e.printStackTrace(System.err);
            } else {
                System.err.printf("Fatal error: %s. Use --verbose for more info.\n", e.getMessage());
            }
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("findcardname", options, true);
    }

    private static Options createOptions() {
        Options options = new Options();
        OptionGroup group = new OptionGroup();
        group.setRequired(true);

        Option help = OptionBuilder
                .withDescription("prints this message")
                .withLongOpt("help")
                .create(OPT_HELP);
        group.addOption(help);

        Option verbose = OptionBuilder
                .withDescription("verbose output")
                .withLongOpt("verbose")
                .create(OPT_VERBOSE_OUTPUT);
        options.addOption(verbose);

        Option ignoreErrors = OptionBuilder
                .withDescription("ignore errors in config file")
                .withLongOpt("ignore-errors")
                .create(OPT_IGNORE_PARSING_ERRORS);
        options.addOption(ignoreErrors);

        Option quiet = OptionBuilder
                .withDescription("do not show parsing errors")
                .withLongOpt("quiet")
                .create(OPT_QUIET);
        options.addOption(quiet);

        Option config = OptionBuilder
                .withDescription("load configuration file")
                .withArgName("config-file")
                .hasArg()
                .withLongOpt("config")
                .isRequired()
                .create(OPT_CONFIG_FILE);
        group.addOption(config);
        options.addOptionGroup(group);

        Option cardnum = OptionBuilder
                .withDescription("find cards by the number. If not specified, card numbers are asked interactively.")
                .withArgName("card,card...")
                .hasArgs(1)
                .hasOptionalArgs()
                .withLongOpt("card")
                .withValueSeparator(',')
                .create(OPT_CARD_NUMBER);
        options.addOption(cardnum);

        return options;
    }

    private void processCards() throws ParseException, IOException {
        // parsing config file
        long t1 = System.nanoTime();
        boolean hasErrors = parser.parseFile(configFile, cardList::add, this::printConfigError);
        long t2 = System.nanoTime();
        profileMsg("Parsing", t1, t2);

        if (hasErrors && !ignoreParsingErrors) {
            System.out.println("Errors found while parsing config. To ignore use --ignore-errors. See --help for all options.");
            return;
        }

        // parsing and checking card numbers
        if (cardNumbers == null) {
            processCardsInteractive();
        } else {
            processCardsFromCmd();
        }
    }

    private void processCardsInteractive() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            parser.processCardsPans(() -> terminalSupplier(reader),
                    this::findCard,
                    this::printCardError);
        }
    }

    private void processCardsFromCmd() {
        Iterator<String> cards = Arrays.asList(cardNumbers).iterator();
        parser.processCardsPans(() -> commandLineSupplier(cards),
                this::findCard,
                this::printCardError);
    }

    private void findCard(String cardNr, Integer pan) {
        long t1 = System.nanoTime();
        String name = cardList.find(pan);
        long t2 = System.nanoTime();
        if (name == null) {
            printCardError(cardNr, "Not found");
        } else {
            System.out.printf("%s: %s\n", cardNr, name);
        }
        profileMsg("Searching", t1, t2);
    }

    private String commandLineSupplier(Iterator<String> cards) {
        if (!cards.hasNext()) {
            return null;
        }
        return cards.next();
    }

    private String terminalSupplier(BufferedReader reader) {
        System.out.print("Enter card number: ");
        try {
            String line = reader.readLine();
            if (line != null && line.trim().isEmpty()) {
                line = null;
            }
            return line;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void printConfigError(Integer lineNo, String msg) {
        if (!quiet) {
            System.err.printf("Error in line %d: %s\n", lineNo, msg);
        }
    }

    private void printCardError(String cardNr, String msg) {
        System.err.printf("Error: %s: %s\n", cardNr, msg);
    }

    private void profileMsg(String msg, long t1, long t2) {
        if (verbose) {
            System.out.printf("%s took %.2f ms\n", msg, (t2 - t1) / 1000000.0);
        }
    }
}