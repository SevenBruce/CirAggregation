package edu.bjut.boudia;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.bjut.boudia.messages.ParamsECC;
import edu.bjut.boudia.messages.RegMessage;
import edu.bjut.boudia.messages.RegMessageFromServer2User;
import edu.bjut.boudia.messages.RepMessage2;
import it.unisa.dia.gas.jpbc.Element;
import edu.bjut.TimeStastic;

public class myMain {

    private static final Logger LOG = LoggerFactory.getLogger(myMain.class);
    private static Out out;
    private static Agg agg;
    private static CC server;
    private static Meters[] meters;

    public static void main(String args[]) throws IOException {

        out = new Out("Boudia_cirAgg_May2st4.time");

        entitiesInitialization();
        aggRegistration();

        dataAggregation();
        TimeStastic.logTime("agg", agg.getStopWatch().getTaskInfo(), LOG);
        TimeStastic.logTime("server", server.getStopWatch().getTaskInfo(), LOG);
        for (int i = 0; i < meters.length; ++i) {
            TimeStastic.logTime("meter_" + i, meters[i].getStopWatch().getTaskInfo(), LOG);
        }
        out.close();
    }

    private static void entitiesInitialization() throws IOException {
        server = new CC();
        ParamsECC ps = server.getParamsECC();
        agg = new Agg(ps);
    }

    private static void aggRegistration() throws IOException {
        RegMessage reg = agg.genRegMesssage();
        server.getAggRegMessage(reg);
    }

    private static void meterRegistration() throws IOException {
        RegMessage reg;
        Element pj;
        RegMessageFromServer2User regCC = server.getPublicKeysAndReferenceNumbers();
        for (int i = 0; i < meters.length; i++) {
            reg = meters[i].genRegMesssage();
            pj = agg.getMeterRegMessage(reg);
            meters[i].getRegBackFromAgg(pj);

            meters[i].getRegFromCC(regCC);
        }
    }

    /**
     * simulate data aggregation meters reporting one type of data
     * to the server analysis. 
     * 
     * @throws IOException
     */
    private static void dataAggregation() throws IOException {

        printAndWrite("Boudia one type of data ");
        for (int meterNumber : Params.ARRAY_OF_METERS_NUM) {
            Params.METERS_NUM = meterNumber;

            meterInitialization(meterNumber);

            getMeterRegTime(meterNumber);
            meterRegistration();
            getMeterRepTime(meterNumber);
        }
    }

    private static void getMeterRegTime(int meterNumber) throws IOException {
        double regTime = 0;
        for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
            regTime += oneTimeMeterRegTime();
        }
        regTime = regTime / 1000000;
        regTime = regTime /Params.EXPERIMENT_TIMES;
        printAndWrite("regreg  when meter number is : " + meterNumber);
        printAndWriteData(regTime);
        printAndWrite("");
    }

    private static void getMeterRepTime(int meterNumber) throws IOException {
        double repTime = 0;
        for (int j = 0; j < Params.EXPERIMENT_TIMES; j++) {
            repTime += oneTimeMeterRepTime();
        }
        repTime = repTime / 1000000;
        repTime = repTime / Params.EXPERIMENT_TIMES;
        printAndWrite("rep  when meter number is : " + meterNumber);
        printAndWriteData(repTime);
        printAndWrite("");
    }

    private static void meterInitialization(int num) throws IOException {
        meters = new Meters[num];
        ParamsECC ps = server.getParamsECC();
        for (int i = 0; i < meters.length; i++) {
            meters[i] = new Meters(ps);
        }
    }

    private static long oneTimeMeterRegTime() throws IOException {
        long sl = System.nanoTime();
        RegMessageFromServer2User regCC = server.getPublicKeysAndReferenceNumbers();
        for (int i = 0; i < meters.length; i++) {
            RegMessage regUser = meters[i].genRegMesssage();
            Element pj = agg.getMeterRegMessage(regUser);
            meters[i].getRegBackFromAgg(pj);

            meters[i].getRegFromCC(regCC);
        }
        long el = System.nanoTime();
        agg.reSetRegMessages();
        return (el - sl);
    }

    private static long oneTimeMeterRepTime() throws IOException {

        long sl = System.nanoTime();
        for (int i = 0; i < Params.METERS_NUM; i++) {
            RepMessage2 repMessage = meters[i].genRepMessage();
            RepMessage2 repAgg = agg.getRepMessage(repMessage);
            server.getRepMessage(repAgg);
        }
        long el = System.nanoTime();
        return (el - sl);
    }

    private static void printAndWriteData(double totalTime) {
        System.out.println(totalTime);
        out.println(totalTime);
    }

    private static void printAndWrite(String outStr) {
        System.out.println(outStr);
        out.println(outStr);
    }

}
