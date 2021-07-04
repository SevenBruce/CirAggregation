package edu.bjut.boudia;

public class Params {

    public final static int EXPERIMENT_TIMES = 100; /* times of running the experiments */
//	public final static int EXPERIMENT_REPEART_TIMES = 20; /* times of running the experiments */
//	public final static int EXPERIMENT_TIMES = 3; /* times of running the experiments */
    
    public static int ARRAY_OF_METERS_NUM[] = {20,30,40,50,60}; /* number of smart meters */
//	public static int ARRAY_OF_METERS_NUM[] = {2,3,4,5,6}; /* number of smart meters */
//	public static int ARRAY_OF_METERS_NUM[] = {3}; /* number of smart meters */
    public static int METERS_NUM; /* number of smart meters */
    
//	public static BigInteger smallMod = new BigInteger("1152921504606846976");
    public static int UPBOUND_LIMIT_OF_METER_DATA = 60; /* upper bound of the reporting system */
    
//	public static int REPORT_UPBOUND_LIMIT = 100000; /* upper bound of the reporting system */
//	public static int LEAPES = 316; /* = square root of LIMIT */
    public static int REPORT_UPBOUND_LIMIT = 3600; /* upper bound of the reporting system */
    public static int LEAPES = 60; /* = square root of LIMIT */
    
}
