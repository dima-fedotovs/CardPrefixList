package test.java.cardprefixlist;

import test.java.cardprefixlist.exceptions.CrossingRangeException;

import java.util.*;

/**
 * @author Dimitrijs Fedotovs
 */
public class CardList {
    TreeMap<Integer, Range> tree = new TreeMap<>();

    public void add(int from, int to, String name) throws CrossingRangeException {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("from and to should not be negative");
        }
        if (name == null) {
            throw new IllegalArgumentException("name should not be null");
        }
        int min = Math.min(from, to);
        int max = Math.max(from, to);
        Range range = new Range(min, max, name);
        if (tree.isEmpty()) {
            putRange(range);
        } else {
            normalizeAndPutRange(range);
        }
    }

    public String find(int pan) {
        Map.Entry<Integer, Range> entry = tree.floorEntry(pan);
        if (entry == null) {
            return null;
        }

        Range range = entry.getValue();

        if (range.contains(pan)) {
            return range.getName();
        } else {
            return null;
        }
    }

    void normalizeAndPutRange(Range newRange) throws CrossingRangeException {
        Collection<Range> affectedRanges = getAffectedRanges(newRange);
        List<Range> toPut = new ArrayList<>();
        outer: {
            for (Range affectedRange : affectedRanges) {
                if (affectedRange.merge(newRange, toPut)) {
                    break outer;
                }
            }
            putRange(newRange);
        }

        toPut.forEach(this::putRange);
    }

    void putRange(Range range) {
        tree.put(range.getPrefixFrom(), range);
    }

    Collection<Range> getAffectedRanges(Range range) throws CrossingRangeException {
        int from = range.getPrefixFrom();
        int to = range.getPrefixTo();
        Map.Entry<Integer, Range> frontEntry = tree.floorEntry(from);
        Map.Entry<Integer, Range> backEntry = tree.floorEntry(to);
        Integer realFrom = null;

        if (frontEntry != null) {
            frontEntry.getValue().assertNotCrossing(range);
            realFrom = frontEntry.getKey();
        }

        if (backEntry != null) {
            backEntry.getValue().assertNotCrossing(range);
        }


        NavigableMap<Integer, Range> subMap;

        if (realFrom == null) {
            // head of ranges should be checked
            subMap = tree.headMap(to, true);
        } else {
            // subset of ranges should be checked
            subMap = tree.subMap(realFrom, true, to, true);
        }
        return subMap.values();
    }

}
