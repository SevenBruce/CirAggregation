package edu.bjut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testNow()
    {
        System.out.println(TimeUtils.Now());
        assertEquals(TimeUtils.Now().length(), TimeUtils.pattern.length() - 2);
    }
}
