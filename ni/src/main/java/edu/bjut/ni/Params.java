package edu.bjut.ni;
public class Params {
    
//	public final static int EXPERIMENT_REPEART_TIMES = 1000; /* times of running the experiments */
    public final static int EXPERIMENT_TIMES = 100; /* times of running the experiments */
//	public final static int EXPERIMENT_TIMES = 1; /* times of running the experiments */
    
    public static int ARRAY_OF_METERS_NUM[] = {20,30,40,50,60}; /* number of smart meters */
//	public static int ARRAY_OF_METERS_NUM[] = {2,3,4,5,6}; /* number of smart meters */
    public static int METERS_NUM;
    public static int SINGLE_METER_REPROTING_RANGE = 60; /* upper bound of a meter's reporting data */
    
    public static int REPORT_UPBOUND_LIMIT = 3600; /* upper bound of the reporting system */
    public static int LEAPES = 60; /* = square root of LIMIT */
}
