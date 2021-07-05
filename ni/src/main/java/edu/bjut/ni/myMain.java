package edu.bjut.ni;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.bjut.TimeStastic;
import edu.bjut.ni.messages.ParamsECC;
import edu.bjut.ni.messages.RegMessage;
import edu.bjut.ni.messages.RepAgg;
import edu.bjut.ni.messages.RepMessage;
import it.unisa.dia.gas.jpbc.Element;

public class myMain {

    private static final Logger LOG = LoggerFactory.getLogger(myMain.class);
    private static Out out;

    private static TA ta;
    private static Agg agg;
    private static OperationCenter center;
    private static Meter[] meters;

    public static void main(String args[]) throws IOException {

        out = new Out("Ni2017_cirAgg_2020May17_1.time");

        dataAggPhase();

        out.close();
        
    }

    /**
     * simulate the data aggregation phase a meter report one type of data to the
     * server for analysis.
     * 
     * @throws IOException
     */
    private static void dataAggPhase() throws IOException {

        printAndWrite("Ni 2017");
        for (int number : Params.ARRAY_OF_METERS_NUM) {

            Params.METERS_NUM = number;
            entitiesInitialization(number);

            getMeterRegtime(number);
            // oneTimeMeterRegTime();
            // getMeterReptime(number);
        }
    }

    private static void getMeterRegtime(int number) throws IOException {
        double repTime = 0;
        for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
            repTime += oneTimeMeterRegTime();
        }
        repTime = repTime / 1000000;
        repTime = repTime / Params.EXPERIMENT_TIMES;

        printAndWrite("reg time when meter number : " + number);
        printAndWriteData(repTime);
    }

    private static void entitiesInitialization(int number) throws IOException {
        ta = new TA(number);
        ParamsECC ps = ta.getParamsECC();

        center = new OperationCenter(ps);
        center.getRegBack(ta.getKeyFromTA());
        agg = new Agg(ps);
        aggregatorRegistration();
        meterIntialiaztion(number);
    }

    private static void getMeterReptime(int number) throws IOException {
        double repTime = 0;
        for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
            repTime += oneTimeMeterRepTime();
        }
        repTime = repTime / 1000000;
        repTime = repTime / Params.EXPERIMENT_TIMES;

        printAndWrite("reporting time when meter number : " + number);
        printAndWriteData(repTime);
        printAndWrite("");
    }

    private static void aggregatorRegistration() throws IOException {
        // TODO Auto-generated method stub
        Element h = center.getH();
        agg.setH(h);
        center.getY(agg.getY());
    }

    private static void printAndWriteData(double totalTime) {
        System.out.println(totalTime);
        out.println(totalTime);
    }

    private static void printAndWrite(String outStr) {
        System.out.println(outStr);
        out.println(outStr);
    }

    private static void meterIntialiaztion(int num) throws IOException {
        meters = new Meter[num];
        ParamsECC ps = ta.getParamsECC();
        for (int i = 0; i < meters.length; i++) {
            meters[i] = new Meter(ps);
        }
    }

    private static void meterReg() throws IOException {
        for (int i = 0; i < meters.length; i++) {
            meters[i].getRegBackTA(ta.getMeterKeys());
            RegMessage reg = meters[i].genResMessage();
            meters[i].getRegBackOC(center.getMeterReg(reg));
        }
    }

    private static long oneTimeMeterRegTime() throws IOException {

        long sl = System.nanoTime();
        for (int i = 0; i < meters.length; i++) {
            meters[i].getRegBackTA(ta.getMeterKeys());
            RegMessage reg = meters[i].genResMessage();
            meters[i].getRegBackOC(center.getMeterReg(reg));
        }
        long el = System.nanoTime();
        ta.reSetIterator();
        return (el - sl);
    }

    private static long oneTimeMeterRepTime() throws IOException {

        long sl = System.nanoTime();
        for (int i = 0; i < Params.METERS_NUM; i++) {
            RepMessage repMessage = meters[i].genRepMessage();
            RepAgg repAgg = agg.getRepMessage(repMessage);
            center.getRepMessage(repAgg);
        }
        long el = System.nanoTime();
        return (el - sl);
    }

}
