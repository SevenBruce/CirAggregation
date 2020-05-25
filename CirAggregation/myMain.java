import java.io.IOException;

import messages.ParamsECC;
import messages.MeterRegBack;
import messages.MeterRegMessage;
import messages.PublicInfo;
import messages.RegBack;
import messages.RegMessage;
import messages.RepMessage;

public class myMain {

	private static Out out;

	private static TTP ttp;
	private static Agg agg;
	private static UtilitySupplier supplier;
	private static Meter[] meter;

	public static void main(String args[]) throws IOException {

		out = new Out("CircleGroup_May12-1.time");
		ttp = new TTP();

		printAndWrite("Circle aggregation");

		aggPhaseWithVaryingMeters();

		aggPhaseWithVaryingK();

		aggPhaseWithVaryingRange();

		Runtime.getRuntime().exec("shutdown -s");
		out.close();
	}

	/**
	 * simulate the data aggregation phase meter number = 60; k is varying in
	 * the range range is of the default range in PublicParam.java
	 * 
	 * @throws IOException
	 */
	private static void aggPhaseWithVaryingK() throws IOException {

		ParamsECC ps = ttp.publishParamsECC();
		Params.METER_NUM = 20;
		Params.RANGE_SIZE = Params.DEFAULT_RANGE_SIZE;

		utilitySupplier_Ini_And_Reg();
		agg_Ini_And_Reg();
		meterInitialization(ps, Params.METER_NUM);

		for (int k : Params.k_Anonymity) {

			printAndWrite("reg time with K : " + k);
			getMeterRegTime(k);

			meterRegTime(k);
			setPublicInfo();

			printAndWrite("rep time with K : " + k);
			getMeterRepTime();
			ttp.Clear();
		}
		printAndWrite("");
	}

	private static void getMeterRegTime(int k) throws IOException {
		double runningTime = 0;
		for (int count = 0; count < Params.EXPERIMENT_TIMES; count++) {
			runningTime += meterRegTime(k);
			ttp.Clear();
		}
		runningTime = runningTime / 1000000;
		runningTime = runningTime / Params.EXPERIMENT_TIMES;
		printAndWrite(Double.toString(runningTime));
	}

	private static void getMeterRepTime() throws IOException {
		double runningTime = 0;
		for (int count = 0; count < Params.EXPERIMENT_TIMES; count++) {
			runningTime += meterRepTime();
			agg.clearReportMessage();
		}
		runningTime = runningTime / 1000000;
		runningTime = runningTime / Params.EXPERIMENT_TIMES;
		printAndWrite(Double.toString(runningTime));
	}

	/**
	 * simulate the data aggregation phase meter number = 20; range is varying
	 * in the range k is of the default k in PublicParam.java
	 * 
	 * @throws IOException
	 */
	private static void aggPhaseWithVaryingRange() throws IOException {

		ParamsECC ps = ttp.publishParamsECC();
		Params.METER_NUM = 20;
		utilitySupplier_Ini_And_Reg();
		agg_Ini_And_Reg();
		meter_Ini_And_Reg(ps, getDefaultK(), Params.METER_NUM);

		for (int range : Params.RANGE_SIZE_ARRAY) {
			Params.RANGE_SIZE = range;
			setPublicInfo();
			printAndWrite("rep time with range : " + range);
			getMeterRepTime();
		}
		printAndWrite("");
	}

	/**
	 * simulate the data aggregation phase meter number is varying range if of
	 * the default range in PublicParam.java k is of the default k in
	 * PublicParam.java
	 * 
	 * @throws IOException
	 */
	private static void aggPhaseWithVaryingMeters() throws IOException {

		ParamsECC ps = ttp.publishParamsECC();
		Params.RANGE_SIZE = Params.DEFAULT_RANGE_SIZE;
		utilitySupplier_Ini_And_Reg();
		agg_Ini_And_Reg();

		for (int meters : Params.ARRAY_OF_METERS_NUM) {
			Params.METER_NUM = meters;

			meterInitialization(ps, meters);

			printAndWrite("reg time with meters number : " + meters);
			getMeterRegTime(getDefaultK());

			meterRegTime(getDefaultK());
			setPublicInfo();

			printAndWrite("rep time with meters number : " + meters);
			getMeterRepTime();
			ttp.Clear();
		}
		printAndWrite("");
	}

	private static void printAndWrite(String outStr) {
		System.out.println(outStr);
		out.println(outStr);
	}

	private static void meterInitialization(ParamsECC ps, int arraySize) throws IOException {
		meter = new Meter[arraySize];
		for (int i = 0; i < meter.length; i++) {
			meter[i] = new Meter(ps);
		}
	}

	private static void meterRegistration(int k) throws IOException {
		for (int i = 0; i < meter.length; i++) {
			MeterRegMessage reg = meter[i].genRegMesssage();
			ttp.getMeterRegMessage(reg);
		}

		for (int i = 0; i < meter.length; i++) {
			MeterRegBack back = ttp.assignMeterKeys(meter[i].getMeterId(), k);
			meter[i].getRegBack(back);
		}
	}

	private static void meter_Ini_And_Reg(ParamsECC ps, int k, int arraySize) throws IOException {
		meterInitialization(ps, arraySize);
		meterRegistration(k);
	}

	private static int getDefaultK() {
		return Params.DEFAULT_K_ANONYMITY;
	}

	private static void setPublicInfo() throws IOException {
		setAggPublicInfo();
		setMeterPublicInfo();
	}

	private static void setAggPublicInfo() throws IOException {
		PublicInfo pi = supplier.getPublicInfo();
		agg.setNsquare(pi.getNsquare());
	}

	private static void setMeterPublicInfo() throws IOException {
		PublicInfo pi = supplier.getPublicInfo();

		for (int i = 0; i < meter.length; i++) {
			meter[i].setPublicInfo(pi);
		}
	}

	private static void agg_Ini_And_Reg() throws IOException {
		agg = new Agg();

		RegMessage reg = agg.genRegMesssage();
		RegBack back = ttp.getAggregatorRegMessage(reg);
		agg.getRegBack(back);
	}

	private static void utilitySupplier_Ini_And_Reg() throws IOException {
		supplier = new UtilitySupplier();

		RegMessage reg = supplier.genRegMesssage();
		RegBack back = ttp.getUtilitySupplierRegMessage(reg);
		supplier.getRegBack(back);
	}

	// 111
	private static long meterRegTime(int k) throws IOException {
		long sl = System.nanoTime();
		for (int i = 0; i < meter.length; i++) {
			MeterRegMessage reg = meter[i].genRegMesssage();
			ttp.getMeterRegMessage(reg);
		}

		for (int i = 0; i < meter.length; i++) {
			MeterRegBack back = ttp.assignMeterKeys(meter[i].getMeterId(), k);
			meter[i].getRegBack(back);
		}

		setMeterPublicInfo();
		long el = System.nanoTime();
		return (el - sl);
	}

	// 111
	private static long meterRepTime() throws IOException {

		long sl = System.nanoTime();
		for (int i = 0; i < Params.METER_NUM; i++) {
			RepMessage rep = meter[i].genSignedRepMessage();
			RepMessage repAgg = agg.getRepMessage(rep);
			supplier.getRepMessage(repAgg);
		}
		long el = System.nanoTime();
		return (el - sl);
	}

}
