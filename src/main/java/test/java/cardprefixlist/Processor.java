package test.java.cardprefixlist;

import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Processor {
    private final CardList cardList = new CardList();
    private final Parser parser = new Parser();
    private String configFile;
    private Supplier<String> cardNumbersSupplier;
    private BiConsumer<String, Long> profiler;
    private BiConsumer<Integer, String> configErrorReporter;
    private BiConsumer<String, String> cardErrorReporter;
    private BiConsumer<String, String> cardNameReporter;

    public Processor(String configFile, Supplier<String> cardNumbersSupplier) {
        this.configFile = configFile;
        this.cardNumbersSupplier = cardNumbersSupplier;
    }

    public boolean prepare() throws ParseException, IOException {
        // parsing config file
        long t1 = System.nanoTime();
        boolean hasErrors = parser.parseFile(configFile, cardList::add, this::printConfigError);
        long t2 = System.nanoTime();
        profileMsg("Parsing", t1, t2);
        return hasErrors;
    }

    public void process() {
        // parsing and checking card numbers
        parser.processCardsPans(cardNumbersSupplier,
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
            printCardNum(cardNr, name);
        }
        profileMsg("Searching", t1, t2);
    }

    private void printCardNum(String cardNr, String name) {
        if (cardNameReporter != null) {
            cardNameReporter.accept(cardNr, name);
        }
    }


    private void printConfigError(Integer lineNo, String msg) {
        if (configErrorReporter != null) {
            configErrorReporter.accept(lineNo, msg);
        }
    }

    private void printCardError(String cardNr, String msg) {
        if (cardErrorReporter != null) {
            cardErrorReporter.accept(cardNr, msg);
        }
    }

    private void profileMsg(String msg, long t1, long t2) {
        if (profiler != null) {
            profiler.accept(msg, t2 - t1);
        }
    }

    public void setConfigErrorReporter(BiConsumer<Integer, String> configErrorReporter) {
        this.configErrorReporter = configErrorReporter;
    }

    public void setCardErrorReporter(BiConsumer<String, String> cardErrorReporter) {
        this.cardErrorReporter = cardErrorReporter;
    }

    public void setCardNameReporter(BiConsumer<String, String> cardNameReporter) {
        this.cardNameReporter = cardNameReporter;
    }

    public void setProfiler(BiConsumer<String, Long> profiler) {
        this.profiler = profiler;
    }
}
