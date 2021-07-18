package edu.bjut;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import edu.bjut.homo.Utils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void pairTest() {
        // Pairing pairing = PairingFactory.getPairing("cg.properties");
        // Element g = pairing.getG1().newRandomElement().getImmutable();
        // BigInteger x = Utils.randomBig(pairing.getG1().getOrder());
        // Element y = g.mul(x);
        // PairingFactory.getInstance().setReuseInstance(false);
        for (int step = 20; step > 10; step -= 10) {
            int len = step;
            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                ids.add(Utils.randomlong());
            }

            List<Element> eList = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                Pairing pairing = PairingFactory.getPairing("cg.properties");
                eList.add(Utils.hash2Element(ids.get(i), pairing));
            }
            List<Element> pairs = new ArrayList<>();
            List<Element> pairsy = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                Pairing pairing = PairingFactory.getPairing("cg.properties");
                BigInteger b = Utils.randomBig(pairing.getG1().getOrder());
                Element g = pairing.getG1().newRandomElement().getImmutable();
                pairsy.add(g.mul(b));
            }

            StopWatch stopWatch = new StopWatch();
            for (int i = 0; i < len; ++i) {
                Pairing pairing = PairingFactory.getPairing("cg.properties");
                stopWatch.start();
                pairs.add(pairing.pairing(pairsy.get(i), eList.get(i)));
                stopWatch.stop();
                // System.out.println(stopWatch.getLastTaskTimeMillis());
            }
            System.out.println(stopWatch.getTotalTimeMillis() * 1.0 / len);
        }
        assertTrue(true);
    }
}
