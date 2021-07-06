package edu.bjut;

import org.slf4j.Logger;
import org.springframework.util.StopWatch.TaskInfo;

public class TimeStastic {

    public static void logTime(String name, TaskInfo[] infos, Logger LOG) {
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder timeBuilder = new StringBuilder();
        nameBuilder.append(name + ":");
        timeBuilder.append(name + ":");
        for (int i = 0; i < infos.length - 1; ++i) {
            nameBuilder.append(infos[i].getTaskName() + ",");
            timeBuilder.append(infos[i].getTimeNanos() + ",");
        }
        if (infos.length > 0) {
            nameBuilder.append(infos[infos.length - 1].getTaskName());
            timeBuilder.append(infos[infos.length - 1].getTimeNanos());
        }
        LOG.info(nameBuilder.toString());
        LOG.info(timeBuilder.toString());
    }
    
}
