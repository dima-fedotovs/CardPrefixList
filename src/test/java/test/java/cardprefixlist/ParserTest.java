package test.java.cardprefixlist;

import org.junit.Test;
import test.java.cardprefixlist.exceptions.ParsingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParserTest {

    @Test
    public void testParseLine() throws Exception {
        Parser p = new Parser();
        Range r = new Range();
        p.parseLine("123456,654321,xxx", r::init);
        assertEquals(new Range(123456, 654321, "xxx"), r);

        p.parseLine("    123456  ,  999999 , xxx   ", r::init);
        assertEquals(new Range(123456, 999999, "xxx"), r);
    }

    @Test
    public void testParsePrefix() throws Exception {
        Parser p = new Parser();
        assertEquals(123456, p.parsePrefix("123456"));
        assertEquals(123, p.parsePrefix("000123"));
        assertEquals(675849, p.parsePrefix("675849 "));
        assertEquals(675849, p.parsePrefix(" 675849 "));
        assertEquals(675849, p.parsePrefix("     675849"));
        try {
            p.parsePrefix("a");
            fail();
        } catch (ParsingException ex) {
        }
        try {
            p.parsePrefix(" 1234567");
            fail();
        } catch (ParsingException ex) {
        }
    }
}