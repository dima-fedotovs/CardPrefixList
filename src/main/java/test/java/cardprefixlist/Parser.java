package test.java.cardprefixlist;

import test.java.cardprefixlist.exceptions.ParsingException;
import test.java.cardprefixlist.exceptions.ProcessingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author Dimitrijs Fedotovs
 */
public class Parser {

    private static final Pattern cardPattern = Pattern.compile("^\\d{12,24}$");
    private static final Pattern panPattern = Pattern.compile("^\\d{6}$");

    public boolean parseFile(String fileName, ParsedLineConsumer consumer, BiConsumer<Integer, String> errorConsumer) throws IOException {
        boolean hasErrors = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();
                try {
                    parseLine(line, consumer);
                } catch (ProcessingException ex) {
                    hasErrors = true;
                    errorConsumer.accept(lineNo, ex.getMessage());
                }
            }
        }
        return hasErrors;
    }

    public void parseLine(String line, ParsedLineConsumer consumer) throws ProcessingException {
        String[] parts = line.split(",", 3);
        if (parts.length < 3) {
            throw new ParsingException("Invalid format");
        }
        int from = parsePrefix(parts[0]);
        int to = parsePrefix(parts[1]);
        String name = parts[2];
        consumer.accept(from, to, name);
    }

    public int parsePrefix(String pan) throws ProcessingException {
        pan = pan.trim();
        if (!panPattern.matcher(pan).matches()) {
            throw new ParsingException("PAN should contain numbers only and should be 6 chars long");
        }
        return Integer.parseInt(pan);
    }

    public void processCardsPans(Supplier<String> supplier, BiConsumer<String, Integer> consumer, BiConsumer<String, String> errorConsumer) {
        String cardNr;
        while ((cardNr = supplier.get()) != null) {
            try {
                validateCardNumber(cardNr);
                String strPan = cardNr.substring(0, 6);
                int pan = Integer.parseInt(strPan);
                consumer.accept(cardNr, pan);
            } catch (ProcessingException ex) {
                errorConsumer.accept(cardNr, ex.getMessage());
            }
        }
    }

    private void validateCardNumber(String cardNr) throws ProcessingException {
        if (!cardPattern.matcher(cardNr).matches()) {
            throw new ParsingException("Card should contain numbers only and should be 12-24 chars long");
        }
    }

    @FunctionalInterface
    public static interface ParsedLineConsumer {
        public void accept(int from, int to, String name) throws ProcessingException;
    }
}