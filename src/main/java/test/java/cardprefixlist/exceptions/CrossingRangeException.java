package test.java.cardprefixlist.exceptions;

import test.java.cardprefixlist.Range;

/**
 * @author Dimitrijs Fedotovs
 */
public class CrossingRangeException extends ProcessingException {
    private final Range newRange;
    private final Range oldRange;

    public CrossingRangeException(Range newRange, Range oldRange) {
        super("Ranges cannot cross: '" + newRange.getName() + "' and '" + oldRange.getName() + "'");
        this.newRange = newRange;
        this.oldRange = oldRange;
    }

    public Range getNewRange() {
        return newRange;
    }

    public Range getOldRange() {
        return oldRange;
    }
}
