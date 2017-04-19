package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class DSpark {

	static int deadline;
	static double profileTime;
	static double deadlineThreshold=1.50;
	static int inputRatio=10;
	static File inputDirectory;
	static ArrayList<Double> inputSizes = new ArrayList<Double>();
	//static double[] inputSizes = new double[3];
	static long applicationInputSize;
	
	
	//Algorithm for Deadline-aware spark applications with optimized cluster usage 
	static void DSparkAlgo()
	{
		//generate RAS List
		GenerateRAS rasgenObj = new GenerateRAS();
		rasgenObj.rasgen();

		ProfilerDeployer profDepObj = new ProfilerDeployer();
		LogParser logParserObj = new LogParser();
		inputDirectory = new File(Settings.inputPathProfiler);
		
		
		//profile the application with the given profiler inputs
		//for(int i=0;i<Settings.reprofileSize;i++)
		for(int i=0;i<Settings.reprofileSize;i++)
		{
			//make profiling input directory 2x of current size
			if(i>0)		
				make2xInputSize(i);
			
			inputSizes.add((double)inputSize()/1024.0/1024.0);
			
			System.out.println("***Profiling Input Directory Size: "+inputSizes.get(i)+"MB");
			
			profDepObj.submitApps(i*Profiler.configList.size()*Settings.reprofileSize);
			
			//Parse the logs for profiling runs of this step
			logParserObj.parseLog();
			
			
			/*
			//calculate the total time needed for profiling
			profileTime=0;
			for(int j=0;j<Profiler.configList.size();j++)
			{
				profileTime+=Profiler.configList.get(j).getCompletionTimei(0);

			}
			
			System.out.println("Profiling Time: "+profileTime);
			*/
		}

		output();
		//print the configs with  reprofiling outputs
		//Profiler.printConfigList();

		System.out.println("\n\n***RESULTS***\n\nApplicaiton Input Size is: "+applicationInputSize*1024+"MB");
		System.out.println("Profiling input sizes: ");
		for(int i=0;i<Settings.reprofileSize;i++)
		{
			System.out.println(inputSizes.get(i)+" MB");
		}
		/*
		//curve fit //input size vs completion time 
		CurveFitter.FitCurves();
		//approximate completion time for the whole input
		System.out.println("\n\n***RESULTS***\n\nApplicaiton Input Size is: "+applicationInputSize*1024+"MB");
		System.out.println("Profiling input sizes: ");
		for(int i=0;i<Settings.reprofileSize;i++)
		{
			System.out.println(inputSizes.get(i)+" MB");
		}
		for(int i=0;i<Profiler.configList.size();i++)
		{
			double totalTime = Profiler.configList.get(i).getP1()*applicationInputSize*1024+Profiler.configList.get(i).getP2();
			System.out.println("Config "+(i+1)+"-> P1: "+Profiler.configList.get(i).getP1()+" P2: "+Profiler.configList.get(i).getP2());
			System.out.println(" Predicted time for Config-> "+Profiler.configList.get(i).getCore()+" "+Profiler.configList.get(i).getMemory()+" "+Profiler.configList.get(i).getMaxCore()+" is: "+totalTime+" s");
		}
		*/

	}

	private static void make2xInputSize(int i) {
		for (File file : inputDirectory.listFiles()) {
			if (file.isFile())
			{
				runCommand("cp "+file+" "+file+i);
			}
		}
	}

	public static long inputSize() {
		long length = 0;
		for (File file : inputDirectory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += inputSize();
		}
		return length;
	}

	static void output()
	{
		FileWriter fw = null;
		try {
			fw = new FileWriter("output.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<Profiler.configList.size();i++)
		{
			for(int j=0;j<Profiler.configList.get(i).completionTime.size();j++)
			{
				try {
					fw.write(Profiler.configList.get(i).completionTime.get(j)/1000+" "+Profiler.configList.get(i).getTotalCores()+" "+Profiler.configList.get(i).getTotalMemory()+" "+inputSizes.get(j/Settings.reprofileSize)+" "+Profiler.configList.get(i).getTotalExecs()+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	static void runCommand(String cmd)
	{
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		try {
			pr = run.exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pr.waitFor();
			//break;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@SuppressWarnings("resource")
	public static void main(String[] args) {
		/*
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		System.out.println("Please enter the application deadline in seconds");
		deadline=sc.nextInt();
		deadline/=inputRatio;
		deadline*=1000;
		deadline+=268073;
		System.out.println("Deadline: "+deadline);*/
		System.out.println("Please enter the input size of the whole application");
		Scanner sc = new Scanner(System.in);
		applicationInputSize = sc.nextLong();

		DSparkAlgo();

	}
}
