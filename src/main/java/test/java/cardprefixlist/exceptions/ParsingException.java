package test.java.cardprefixlist.exceptions;

/**
 * @author Dimitrijs Fedotovs
 */
public class ParsingException extends ProcessingException {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
