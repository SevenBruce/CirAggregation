package edu.bjut.homo;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import edu.bjut.TimeStastic;
import edu.bjut.TimeUtils;
import edu.bjut.homo.messages.ParamsECC;
import edu.bjut.homo.messages.PublicInfo;
import edu.bjut.homo.messages.RegBack;
import edu.bjut.homo.messages.RegMessage;
import edu.bjut.homo.messages.RepMessage;

public class myMain {

    private static final Logger LOG = LoggerFactory.getLogger(myMain.class);
    private static long sl;
    private static long el;
    private static Out out;
    private static KGC kgc;
    private static Agg agg;
    private static Server server;
    private static SmartMeter[] sm;

    public static void main(String args[]) throws IOException {

        out = new Out("Homo_" + TimeUtils.Now() + ".time");

        entitiesInitialization();
        serverRegistration();
        aggregatorRegistration();

        normalReportingPhase();
        out.close();
    }
    
    private static void entitiesInitialization() throws IOException {
        kgc = new KGC();
        ParamsECC ps = kgc.publishParamsECC();
        
        server = new Server(ps);
        agg = new Agg(ps);
    }

    /**
     * simulate the normal reporting phase meters reporting only one data to the
     * server analysis. one one smart meter reporting messages to the aggregator
     * 
     * @throws IOException
     */
    private static void normalReportingPhase() throws IOException {
        for (int meters : Params.ARRAY_OF_METERS_NUM) {
            Params.METERS_NUM = meters;

            meterIntitaliaztion(meters);
            PublicInfo pi = server.getPublicInfo();
            setMeterPublicInfo(pi);

            printAndWrite("reg time with meters number : " + meters);
            printAndWriteData(getMeterRegTime());
            printAndWrite("");

            printAndWrite("rep time with meters number : " + meters);
            printAndWriteData(getMeterRepTime());
            printAndWrite("");
        }
    }

    private static double getMeterRepTime() throws IOException {
        int count = 0;
        double totalTime = 0;

        while (count < Params.EXPERIMENT_TIMES) {
            totalTime += oneTimeMeterRepTime(1);
            agg.clearReportMessage();
            count++;
        }
        totalTime = totalTime/1000000;
        totalTime = totalTime/Params.EXPERIMENT_TIMES;
        return totalTime;
    }

    private static double getMeterRegTime() throws IOException {
        int count = 0;
        double totalTime = 0;

        while (count < Params.EXPERIMENT_TIMES) {
            totalTime += meterRegTime();
            agg.clearReportMessage();
            count++;
        }
        totalTime = totalTime/1000000;
        totalTime = totalTime/Params.EXPERIMENT_TIMES;
        return totalTime;
    }

    private static void printAndWrite(String outStr) {
        System.out.println(outStr);
        out.println(outStr);
    }
    private static void printAndWriteData(double totalTime) {
        System.out.println(totalTime);
        out.println(totalTime);
    }

    private static void meterIntitaliaztion(int meters) throws IOException {
        sm = new SmartMeter[meters];
        ParamsECC ps = kgc.publishParamsECC();
        for (int i = 0; i < sm.length; i++) {
            sm[i] = new SmartMeter(ps);
        }
    }

//	private static void meterRegistration() throws IOException {
//		for (int i = 0; i < sm.length; i++) {
//			RegMessage reg = sm[i].genRegMesssage();
//			RegBack back = kgc.getRegMessage(reg);
//			sm[i].getRegBack(back);
//		}
//	}

    private static void setMeterPublicInfo(PublicInfo pi) throws IOException {
        for (int i = 0; i < sm.length; i++) {
            sm[i].setPublicInfo(pi);
            sm[i].setAggId(agg.getId());
        }
        agg.setNsquare(pi.getNsquare());
    }

    private static void aggregatorRegistration() throws IOException {
        RegMessage reg = agg.genRegMesssage();
        RegBack back = kgc.getRegMessage(reg);
        agg.getRegBack(back);
        agg.setServerId(server.getId());
    }

    private static void serverRegistration() throws IOException {
        RegMessage reg = server.genRegMesssage();
        RegBack back = kgc.getRegMessage(reg);
        server.getRegBack(back);
    }

    private static long meterRegTime() throws IOException {
        LOG.info("meterRegTime");
        // time stastic init
        kgc.setStopWatch(new StopWatch("kgc"));
        for (int i = 0; i < sm.length; ++i) {
            sm[i].setStopWatch(new StopWatch("meter_"+sm[i].getId()));
        }

        sl = System.nanoTime();
        for (int i = 0; i < Params.METERS_NUM; i++) {
            RegMessage reg = sm[i].genRegMesssage();
            RegBack back = kgc.getRegMessage(reg);
            sm[i].getRegBack(back);
        }
        el = System.nanoTime();
        // time stastic log output
        TimeStastic.logTime("kgc", kgc.getStopWatch().getTaskInfo(), LOG);
        for (int i = 0; i < sm.length; ++i) {
            TimeStastic.logTime(sm[i].getStopWatch().getId(), sm[i].getStopWatch().getTaskInfo(), LOG);
        }
        // total
        long total = kgc.getStopWatch().getTotalTimeNanos(); 
        for (SmartMeter s: sm) {
            total += s.getStopWatch().getTotalTimeNanos();
        }
        LOG.debug("meterRegTime:{}", total);
        return (el - sl);
    }

    private static long oneTimeMeterRepTime(int count) throws IOException {
        LOG.info("oneTimeMeterRepTime");
        // time stastic init
        agg.setStopWatch(new StopWatch("agg"));
        server.setStopWatch(new StopWatch("server"));
        for (int i = 0; i < sm.length; ++i) {
            sm[i].setStopWatch(new StopWatch("meter_"+sm[i].getId()));
        }
        sl = System.nanoTime();
        for (int i = 0; i < Params.METERS_NUM; i++) {
            RepMessage repMessage = sm[i].genSingleRepMessage(count);
            RepMessage repAgg = agg.getRepMessage(repMessage);
            server.getRepMessage(repAgg);
        }
        el = System.nanoTime();
        // time stastic log output
        TimeStastic.logTime(agg.getStopWatch().getId(), agg.getStopWatch().getTaskInfo(), LOG);
        TimeStastic.logTime(server.getStopWatch().getId(), server.getStopWatch().getTaskInfo(), LOG);
        for (int i = 0; i < sm.length; ++i) {
            TimeStastic.logTime(sm[i].getStopWatch().getId(), sm[i].getStopWatch().getTaskInfo(), LOG);
        }
        // total
        long total = agg.getStopWatch().getTotalTimeNanos() + server.getStopWatch().getTotalTimeNanos();
        for (SmartMeter s: sm) {
            total += s.getStopWatch().getTotalTimeNanos();
        }
        LOG.debug("oneTimeMeterRepTime:{}", total);
        return (el - sl);
    }

}
