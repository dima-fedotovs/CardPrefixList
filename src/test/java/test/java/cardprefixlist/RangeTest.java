package test.java.cardprefixlist;

import org.junit.Test;
import test.java.cardprefixlist.exceptions.CrossingRangeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void testGetFromPointLocation_Range() throws Exception {
        Range range = new Range(10, 100, "test");
        assertSame(PointLocation.BEFORE, range.getFromPointLocation(5));
        assertSame(PointLocation.BEFORE, range.getFromPointLocation(9));
        assertSame(PointLocation.AT_START, range.getFromPointLocation(10));
        assertSame(PointLocation.INSIDE, range.getFromPointLocation(11));
        assertSame(PointLocation.INSIDE, range.getFromPointLocation(50));
        assertSame(PointLocation.INSIDE, range.getFromPointLocation(99));
        assertSame(PointLocation.AT_END, range.getFromPointLocation(100));
        assertSame(PointLocation.AFTER, range.getFromPointLocation(101));
        assertSame(PointLocation.AFTER, range.getFromPointLocation(200));
    }

    @Test
    public void testGetFromPointLocation_Point() throws Exception {
        Range range = new Range(50, 50, "test");
        assertSame(PointLocation.BEFORE, range.getFromPointLocation(5));
        assertSame(PointLocation.BEFORE, range.getFromPointLocation(49));
        assertSame(PointLocation.AT_START, range.getFromPointLocation(50));
        assertSame(PointLocation.AFTER, range.getFromPointLocation(51));
        assertSame(PointLocation.AFTER, range.getFromPointLocation(200));
    }

    @Test
    public void testGetToPointLocation_Range() throws Exception {
        Range range = new Range(10, 100, "test");
        assertSame(PointLocation.BEFORE, range.getToPointLocation(5));
        assertSame(PointLocation.BEFORE, range.getToPointLocation(9));
        assertSame(PointLocation.AT_START, range.getToPointLocation(10));
        assertSame(PointLocation.INSIDE, range.getToPointLocation(11));
        assertSame(PointLocation.INSIDE, range.getToPointLocation(50));
        assertSame(PointLocation.INSIDE, range.getToPointLocation(99));
        assertSame(PointLocation.AT_END, range.getToPointLocation(100));
        assertSame(PointLocation.AFTER, range.getToPointLocation(101));
        assertSame(PointLocation.AFTER, range.getToPointLocation(200));
    }

    @Test
    public void testGetToPointLocation_Point() throws Exception {
        Range range = new Range(50, 50, "test");
        assertSame(PointLocation.BEFORE, range.getToPointLocation(5));
        assertSame(PointLocation.BEFORE, range.getToPointLocation(49));
        assertSame(PointLocation.AT_END, range.getToPointLocation(50));
        assertSame(PointLocation.AFTER, range.getToPointLocation(51));
        assertSame(PointLocation.AFTER, range.getToPointLocation(200));
    }

    @Test
    public void testMerge_Range_Range_BEFORE_BEFORE() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(1, 5, "new"), true,
                PointLocation.BEFORE, PointLocation.BEFORE, true,
                new Range(10, 20, "old"),
                new Range(1, 5, "new"),
                new Range(1, 5, "new"));
    }

    @Test
    public void testMerge_Range_Point_BEFORE_BEFORE() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(5, 5, "new"), false,
                PointLocation.BEFORE, PointLocation.BEFORE, true,
                new Range(10, 20, "old"),
                new Range(5, 5, "new"),
                new Range(5, 5, "new"));
    }

    @Test
    public void testMerge_Point_Range_BEFORE_BEFORE() throws Exception {
        test(new Range(10, 10, "old"), false,
                new Range(1, 5, "new"), true,
                PointLocation.BEFORE, PointLocation.BEFORE, true,
                new Range(10, 10, "old"),
                new Range(1, 5, "new"),
                new Range(1, 5, "new"));
    }

    @Test
    public void testMerge_Point_Point_BEFORE_BEFORE() throws Exception {
        test(new Range(15, 15, "old"), false,
                new Range(1, 1, "new"), false,
                PointLocation.BEFORE, PointLocation.BEFORE, true,
                new Range(15, 15, "old"),
                new Range(1, 1, "new"),
                new Range(1, 1, "new"));
    }

    @Test(expected = CrossingRangeException.class)
    public void testMerge_Range_Range_BEFORE_AT_START() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(1, 10, "new"), true,
                PointLocation.BEFORE, PointLocation.AT_START, true,
                new Range(0, 0, ""),
                new Range(0, 0, ""),
                new Range(0, 0, ""));
    }

    @Test(expected = CrossingRangeException.class)
    public void testMerge_Range_Range_BEFORE_INSIDE() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(1, 15, "new"), true,
                PointLocation.BEFORE, PointLocation.INSIDE, true,
                new Range(0, 0, ""),
                new Range(0, 0, ""),
                new Range(0, 0, ""));
    }

    @Test
    public void testMerge_Range_Range_BEFORE_AT_END() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(1, 20, "new"), true,
                PointLocation.BEFORE, PointLocation.AT_END, true,
                new Range(10, 20, "old"),
                new Range(1, 9, "new"),
                new Range(1, 9, "new"));
    }

    @Test

    public void testMerge_Point_Range_BEFORE_AT_END() throws Exception {
        test(new Range(20, 20, "old"), false,
                new Range(1, 20, "new"), true,
                PointLocation.BEFORE, PointLocation.AT_END, true,
                new Range(20, 20, "old"),
                new Range(1, 19, "new"),
                new Range(1, 19, "new"));
    }

    @Test
    public void testMerge_Range_Range_BEFORE_AFTER() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(1, 30, "new"), true,
                PointLocation.BEFORE, PointLocation.AFTER, false,
                new Range(10, 20, "old"),
                new Range(21, 30, "new"),
                new Range(1, 9, "new"));
    }

    @Test
    public void testMerge_Point_Range_BEFORE_AFTER() throws Exception {
        test(new Range(20, 20, "old"), false,
                new Range(1, 30, "new"), true,
                PointLocation.BEFORE, PointLocation.AFTER, false,
                new Range(20, 20, "old"),
                new Range(21, 30, "new"),
                new Range(1, 19, "new"));
    }

    @Test
    public void testMerge_Range_Point_AT_START_AT_START() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(10, 10, "new"), false,
                PointLocation.AT_START, PointLocation.AT_START, true,
                new Range(11, 20, "old"),
                new Range(10, 10, "new"),
                new Range(10, 10, "new"),
                new Range(11, 20, "old"));
    }

    @Test
    public void testMerge_Range_Range_AT_START_INSIDE() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(10, 15, "new"), true,
                PointLocation.AT_START, PointLocation.INSIDE, true,
                new Range(16, 20, "old"),
                new Range(10, 15, "new"),
                new Range(10, 15, "new"),
                new Range(16, 20, "old"));
    }

    @Test
    public void testMerge_Range_Range_AT_START_AT_END() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(10, 20, "new"), true,
                PointLocation.AT_START, PointLocation.AT_END, true,
                new Range(10, 20, "old"),
                new Range(10, 20, "new"));
    }

    @Test
    public void testMerge_Point_Point_AT_START_AT_END() throws Exception {
        test(new Range(10, 10, "old"), false,
                new Range(10, 10, "new"), false,
                PointLocation.AT_START, PointLocation.AT_END, true,
                new Range(10, 10, "old"),
                new Range(10, 10, "new"));
    }

    @Test
    public void testMerge_Range_Range_AT_START_AFTER() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(10, 30, "new"), true,
                PointLocation.AT_START, PointLocation.AFTER, false,
                new Range(10, 20, "old"),
                new Range(21, 30, "new"));
    }

    @Test
    public void testMerge_Point_Range_AT_START_AFTER() throws Exception {
        test(new Range(10, 10, "old"), false,
                new Range(10, 30, "new"), true,
                PointLocation.AT_START, PointLocation.AFTER, false,
                new Range(10, 10, "old"),
                new Range(11, 30, "new"));
    }

    @Test
    public void testMerge_Range_Range_INSIDE_INSIDE() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(11, 29, "new"), true,
                PointLocation.INSIDE, PointLocation.INSIDE, true,
                new Range(10, 10, "old"),
                new Range(11, 29, "new"),
                new Range(10, 10, "old"),
                new Range(11, 29, "new"),
                new Range(30, 30, "old"));
    }

    @Test
    public void testMerge_Range_Point_INSIDE_INSIDE() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(20, 20, "new"), false,
                PointLocation.INSIDE, PointLocation.INSIDE, true,
                new Range(10, 19, "old"),
                new Range(20, 20, "new"),
                new Range(10, 19, "old"),
                new Range(20, 20, "new"),
                new Range(21, 30, "old"));
    }

    @Test
    public void testMerge_Range_Range_INSIDE_AT_END() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(11, 30, "new"), true,
                PointLocation.INSIDE, PointLocation.AT_END, true,
                new Range(10, 10, "old"),
                new Range(11, 30, "new"),
                new Range(10, 10, "old"),
                new Range(11, 30, "new"));
    }

    @Test(expected = CrossingRangeException.class)
    public void testMerge_Range_Range_INSIDE_AFTER() throws Exception {
        test(new Range(10, 20, "old"), true,
                new Range(15, 25, "new"), true,
                PointLocation.INSIDE, PointLocation.AFTER, true,
                new Range(0, 0, ""),
                new Range(0, 0, ""),
                new Range(0, 0, ""));
    }

    @Test
    public void testMerge_Range_Point_AT_END_AT_END() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(30, 30, "new"), false,
                PointLocation.AT_END, PointLocation.AT_END, true,
                new Range(10, 29, "old"),
                new Range(30, 30, "new"),
                new Range(10, 29, "old"),
                new Range(30, 30, "new"));
    }

    @Test(expected = CrossingRangeException.class)
    public void testMerge_Range_Range_AT_END_AFTER() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(30, 35, "new"), true,
                PointLocation.AT_END, PointLocation.AFTER, true,
                new Range(0, 0, ""),
                new Range(0, 0, ""),
                new Range(0, 0, ""));
    }

    @Test
    public void testMerge_Range_Range_AFTER_AFTER() throws Exception {
        test(new Range(10, 30, "old"), true,
                new Range(35, 40, "new"), true,
                PointLocation.AFTER, PointLocation.AFTER, false,
                new Range(10, 30, "old"),
                new Range(35, 40, "new"));
    }

    private void test(Range oldRange, boolean oldRangeIsRange,
                      Range newRange, boolean newRangeIsRange,
                      PointLocation expectedStartLocation, PointLocation expectedEndLocation, boolean expectedResult,
                      Range expectedOldRange,
                      Range expectedNewRange,
                      Range... expectedUpdates) throws Exception {
        assertTrue(oldRange.getPrefixFrom() <= oldRange.getPrefixTo());
        assertTrue(newRange.getPrefixFrom() <= newRange.getPrefixTo());
        assertEquals(oldRangeIsRange, oldRange.getPrefixFrom() < oldRange.getPrefixTo());
        assertEquals(newRangeIsRange, newRange.getPrefixFrom() < newRange.getPrefixTo());

        assertEquals(expectedStartLocation, oldRange.getFromPointLocation(newRange.getPrefixFrom()));
        assertEquals(expectedEndLocation, oldRange.getToPointLocation(newRange.getPrefixTo()));

        List<Range> updates = new ArrayList<>();
        assertEquals(expectedResult, oldRange.merge(newRange, updates));
        assertEquals(expectedOldRange, oldRange);
        assertEquals(expectedNewRange, newRange);
        // order doesn't matter
        assertEquals(new HashSet<Range>(Arrays.asList(expectedUpdates)), new HashSet<Range>(updates));
    }

    @Test
    public void testAssertNotCrossing() throws Exception {
        // range & range
        checkAssertNotCrossing(true, new Range(10, 20, "t1"), new Range(9, 15, "t2"));
        checkAssertNotCrossing(true, new Range(10, 20, "t1"), new Range(9, 10, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(10, 15, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(11, 15, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(10, 20, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(15, 20, "t2"));
        checkAssertNotCrossing(true, new Range(10, 20, "t1"), new Range(15, 21, "t2"));
        checkAssertNotCrossing(true, new Range(10, 20, "t1"), new Range(20, 21, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(21, 22, "t2"));
        checkAssertNotCrossing(false, new Range(10, 20, "t1"), new Range(1, 22, "t2"));
        // point & range
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(1, 19, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(1, 20, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(1, 21, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(20, 22, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(21, 22, "t2"));
        // range & point
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(14, 14, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(15, 15, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(16, 16, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(17, 17, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(19, 19, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(20, 20, "t2"));
        checkAssertNotCrossing(false, new Range(15, 20, "t1"), new Range(21, 21, "t2"));
        // point & point
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(14, 14, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(18, 18, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(19, 19, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(20, 20, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(21, 21, "t2"));
        checkAssertNotCrossing(false, new Range(20, 20, "t1"), new Range(22, 22, "t2"));
    }

    private void checkAssertNotCrossing(boolean shouldFail, Range r1, Range r2) throws Exception {
        try {
            r1.assertNotCrossing(r2);
            if (shouldFail) {
                fail();
            }
        } catch (CrossingRangeException ex) {
            if (!shouldFail) {
                fail();
            }
        }
    }
}