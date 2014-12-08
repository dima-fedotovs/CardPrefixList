package test.java.cardprefixlist;

import test.java.cardprefixlist.exceptions.CrossingRangeException;

import java.util.List;

/**
 * @author Dimitrijs Fedotovs
 */
public class Range {
    private int prefixFrom;
    private int prefixTo;
    private String name;

    public Range(int prefixFrom, int prefixTo, String name) {
        this.prefixFrom = prefixFrom;
        this.prefixTo = prefixTo;
        this.name = name;
    }

    public int getPrefixFrom() {
        return prefixFrom;
    }

    public void setPrefixFrom(int prefixFrom) {
        this.prefixFrom = prefixFrom;
    }

    public int getPrefixTo() {
        return prefixTo;
    }

    public void setPrefixTo(int prefixTo) {
        this.prefixTo = prefixTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointLocation getFromPointLocation(int point) {
        if (prefixFrom > point) {
            return PointLocation.BEFORE;
        } else if (prefixFrom == point) {
            return PointLocation.AT_START;
        } else if (prefixTo == point) {
            return PointLocation.AT_END;
        } else if (prefixTo < point) {
            return PointLocation.AFTER;
        } else {
            return PointLocation.INSIDE;
        }
    }

    public PointLocation getToPointLocation(int point) {
        if (prefixTo < point) {
            return PointLocation.AFTER;
        } else if (prefixTo == point) {
            return PointLocation.AT_END;
        } else if (prefixFrom == point) {
            return PointLocation.AT_START;
        } else if (prefixFrom > point) {
            return PointLocation.BEFORE;
        } else {
            return PointLocation.INSIDE;
        }
    }

    /**
     * @param newRange
     * @param updates
     * @return true if newRange is fully processed.
     */
    public boolean merge(Range newRange, List<Range> updates) throws CrossingRangeException {
        PointLocation locationFrom = getFromPointLocation(newRange.getPrefixFrom());
        PointLocation locationTo = getToPointLocation(newRange.getPrefixTo());

        switch (locationFrom) {
            case BEFORE:
                switch (locationTo) {
                    case BEFORE:
                        updates.add(newRange);
                        return true;
                    case AT_START:
                    case INSIDE:
                        throw new CrossingRangeException(newRange, this);
                    case AT_END:
                        newRange.setPrefixTo(this.getPrefixFrom() - 1);
                        updates.add(newRange);
                        return true;
                    case AFTER:
                        Range r1 = new Range(newRange.getPrefixFrom(), getPrefixFrom() - 1, newRange.getName());
                        newRange.setPrefixFrom(getPrefixTo() + 1);
                        updates.add(r1);
                        return false;
                    default:
                        throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
                }
            case AT_START:
                switch (locationTo) {
                    case AT_START:
                    case INSIDE:
                        this.setPrefixFrom(newRange.getPrefixTo() + 1);
                        updates.add(newRange);
                        updates.add(this);
                        return true;
                    case AT_END:
                        return true;
                    case AFTER:
                        newRange.setPrefixFrom(this.getPrefixTo() + 1);
                        return false;
                    default:
                        throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
                }
            case INSIDE:
                switch (locationTo) {
                    case INSIDE:
                        Range r1 = new Range(newRange.getPrefixTo() + 1, this.getPrefixTo(), this.getName());
                        this.setPrefixTo(newRange.getPrefixFrom() - 1);
                        updates.add(this);
                        updates.add(newRange);
                        updates.add(r1);
                        return true;
                    case AT_END:
                        this.setPrefixTo(newRange.getPrefixFrom() - 1);
                        updates.add(this);
                        updates.add(newRange);
                        return true;
                    case AFTER:
                        throw new CrossingRangeException(newRange, this);
                    default:
                        throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
                }
            case AT_END:
                switch (locationTo) {
                    case AT_END:
                        this.setPrefixTo(newRange.getPrefixFrom() - 1);
                        updates.add(this);
                        updates.add(newRange);
                        return true;
                    case AFTER:
                        throw new CrossingRangeException(newRange, this);
                    default:
                        throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
                }
            case AFTER:
                switch (locationFrom) {
                    case AFTER:
                        return false;
                    default:
                        throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
                }
            default:
                throw new UnsupportedOperationException(locationFrom.name() + ":" + locationTo.name());
        }

    }

    public boolean contains(int pan) {
        return getPrefixFrom() <= pan && getPrefixTo() >= pan;
    }

    public void assertNotCrossing(Range range) throws CrossingRangeException {
        int nr1 = range.getPrefixFrom();
        int nr2 = range.getPrefixTo();
        int or1 = getPrefixFrom();
        int or2 = getPrefixTo();

        if (nr1 < or1 && nr2 >= or1 && nr2 < or2) {
            throw new CrossingRangeException(range, this);
        }

        if (nr2 > or2 && nr1 <= or2 && nr1 > or1) {
            throw new CrossingRangeException(range, this);
        }

    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + prefixFrom +
                ", to=" + prefixTo +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range that = (Range) o;

        if (prefixFrom != that.prefixFrom) return false;
        if (prefixTo != that.prefixTo) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prefixFrom;
        result = 31 * result + prefixTo;
        result = 31 * result + name.hashCode();
        return result;
    }
}
