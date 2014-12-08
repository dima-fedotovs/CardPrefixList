package test.java.cardprefixlist.exceptions;

/**
 * @author Dimitrijs Fedotovs
 */
public class ProcessingException extends Exception {
    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
