package test.java.cardprefixlist;

import org.junit.Test;
import test.java.cardprefixlist.exceptions.CrossingRangeException;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class CardListTest {

    @Test
    public void testAdd02() throws Exception {
        CardList list = new CardList();

        list.add(111111,222222,"test1");
        checkList(list,
                new Range(111111, 222222, "test1"));
        list.add(222223, 333333, "test2");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"));
        list.add(333334, 888888, "test3");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"),
                new Range(333334, 888888, "test3"));
        list.add(444444, 555555, "test4");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"),
                new Range(333334, 444443, "test3"),
                new Range(444444, 555555, "test4"),
                new Range(555556, 888888, "test3"));
        list.add(888889, 999990, "test5");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"),
                new Range(333334, 444443, "test3"),
                new Range(444444, 555555, "test4"),
                new Range(555556, 888888, "test3"),
                new Range(888889, 999990, "test5"));
        try {
            list.add(111111, 999980, "test6");
            fail();
        } catch (CrossingRangeException ex) {
            assertEquals("test5", ex.getOldRange().getName());
            assertEquals("test6", ex.getNewRange().getName());
        }
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"),
                new Range(333334, 444443, "test3"),
                new Range(444444, 555555, "test4"),
                new Range(555556, 888888, "test3"),
                new Range(888889, 999990, "test5"));
        list.add(111111,555555,"test7");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"),
                new Range(333334, 444443, "test3"),
                new Range(444444, 555555, "test4"),
                new Range(555556, 888888, "test3"),
                new Range(888889, 999990, "test5"));
    }
    @Test
    public void testAdd01() throws Exception {
        CardList list = new CardList();
        list.add(111111, 222222, "test1");
        checkList(list,
                new Range(111111, 222222, "test1"));
        list.add(222223, 333333, "test2");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333333, "test2"));
        list.add(333333, 333333, "test3");
        checkList(list,
                new Range(111111, 222222, "test1"),
                new Range(222223, 333332, "test2"),
                new Range(333333, 333333, "test3"));
        list.add(111555, 200000, "test4");
        checkList(list,
                new Range(111111, 111554, "test1"),
                new Range(111555, 200000, "test4"),
                new Range(200001, 222222, "test1"),
                new Range(222223, 333332, "test2"),
                new Range(333333, 333333, "test3"));
    }

    private void checkList(CardList list, Range... expected) throws Exception {
        Set<Integer> expectedKeys = new TreeSet<>();
        Set<Range> expectedRanges = new HashSet<>();
        for (Range r : expected) {
            expectedKeys.add(r.getPrefixFrom());
            expectedRanges.add(r);
        }
        assertEquals(expectedKeys, list.tree.keySet());
        Set<Range> actualRanges = new HashSet<>(list.tree.values());
        assertEquals(expectedRanges, actualRanges);
    }

    @Test
    public void testFind() throws Exception {
        CardList list = new CardList();
        list.add(111111, 111554, "test1");
        list.add(111555, 200000, "test4");
        list.add(200001, 222222, "test1");
        list.add(222223, 333332, "test2");
        list.add(333333, 333333, "test3");

        assertEquals("test1", list.find(111111));
        assertEquals("test1", list.find(111234));
        assertEquals("test1", list.find(111554));
        assertEquals("test4", list.find(111555));
        assertEquals("test4", list.find(123456));
        assertEquals("test4", list.find(200000));
        assertEquals("test1", list.find(200002));
        assertEquals("test3", list.find(333333));
        assertNull(list.find(333334));
    }
}
