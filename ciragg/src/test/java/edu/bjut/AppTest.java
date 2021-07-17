package edu.bjut;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;
import org.springframework.util.StopWatch;

import edu.bjut.cir.Utils;
import edu.bjut.cir.messages.MeterRegMessage;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    Pairing pairing = PairingFactory.getPairing("cg.properties");
    Element x = pairing.getG1().newRandomElement().getImmutable().mul(Utils.randomBig());
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testPerformance()
    {
        // MeterRegMessage mRegMessage = null;
        // Pairing pairing = PairingFactory.getPairing("cg.properties");
        StopWatch stopWatch = new StopWatch();
        // stopWatch.start();
        // mRegMessage = new MeterRegMessage(1, pairing.getG1().newElement());
        // stopWatch.stop();
        // mRegMessage.getId();
        // System.out.println(stopWatch.getLastTaskTimeNanos());

        // stopWatch.start();
        // mRegMessage = new MeterRegMessage(1, pairing.getG1().newElement());
        // stopWatch.stop();
        // mRegMessage.getId();
        // System.out.println(stopWatch.getLastTaskTimeNanos());
        TestHelper testHelper = new TestHelper(stopWatch);
        for (int i = 0; i < 20; ++i) {
            testHelper.funcTime();
        }

    }

    class TestHelper {

        private StopWatch stopWatch;
        TestHelper(StopWatch stopWatch) {
            this.stopWatch = stopWatch;
        }

        MeterRegMessage funcTime() {
            stopWatch.start();
            MeterRegMessage mRegMessage = new MeterRegMessage(1, pairing.getG1().newElement());
            stopWatch.stop();
            System.out.println(stopWatch.getLastTaskTimeNanos());
            return mRegMessage;
        }
    };
}
