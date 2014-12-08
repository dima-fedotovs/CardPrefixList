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
                boolean ignoreParsingErrors = cmd.hasOption(OPT_IGNORE_PARSING_ERRORS);
                boolean quiet = cmd.hasOption(OPT_QUIET);
                String configFile = cmd.getOptionValue(OPT_CONFIG_FILE);
                String[] cardNumbers = cmd.getOptionValues(OPT_CARD_NUMBER);

                Supplier<String> cardNumbersSupplier = cardNumbers == null ?
                        new InteractiveCardNumberSupplier() :
                        new ListCardNumberSupplier(cardNumbers);
                Processor processor = new Processor(configFile, cardNumbersSupplier);
                processor.setCardNameReporter((cardNr, name) -> System.out.printf("%s: %s\n", cardNr, name));
                processor.setCardErrorReporter((cardNr, msg) -> System.err.printf("Error: %s: %s\n", cardNr, msg));
                if (!quiet) {
                    processor.setConfigErrorReporter((lineNo, msg) -> System.err.printf("Error in line %d: %s\n", lineNo, msg));
                }
                if (verbose) {
                    processor.setProfiler((msg, nano) -> System.out.printf("%s took %.2f ms\n", msg, nano / 1000000.0));
                }
                boolean hasErrors = processor.prepare();
                if (hasErrors && !ignoreParsingErrors) {
                    System.out.println("Errors found while parsing config. To ignore use --ignore-errors. See --help for all options.");
                    System.exit(1);
                    return;
                }
                processor.process();
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
        formatter.printHelp("cardprefixlist", options, true);
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
                .withArgName("card card ...")
                .hasArgs(1)
                .hasOptionalArgs()
                .withLongOpt("card")
                .withValueSeparator()
                .create(OPT_CARD_NUMBER);
        options.addOption(cardnum);

        return options;
    }

    private static class InteractiveCardNumberSupplier implements Supplier<String> {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        @Override
        public String get() {
            String cardNumber = promptCardNumber();
            if (cardNumber == null) {
                return null;
            }
            cardNumber = cardNumber.trim();
            if (cardNumber.isEmpty()) {
                return null;
            }
            return cardNumber.trim();
        }

        public String promptCardNumber() {
            System.out.print("Enter card number: ");
            try {
                return reader.readLine();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static class ListCardNumberSupplier implements Supplier<String> {
        private Iterator<String> numbersIterator;

        public ListCardNumberSupplier(String[] cardNumbers) {
            numbersIterator = Arrays.asList(cardNumbers).iterator();
        }

        @Override
        public String get() {
            if (numbersIterator.hasNext()) {
                return numbersIterator.next().trim();
            } else {
                return null;
            }
        }
    }
}