package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class DSpark {

	static double deadline;
	static double profileTime;
	static double deadlineThreshold=1.50;
	static int inputRatio=10;
	static File inputDirectory;
	static ArrayList<Double> inputSizes;
	//static double[] inputSizes = new double[3];
	static long applicationInputSize;
	
	static void DSparkAlgoLatest()
	{
		//generate RAS List
		GenerateRAS rasgenObj = new GenerateRAS();
		rasgenObj.rasgen();
		
		Collections.sort(Profiler.configList, new Configurations());
		Profiler.printConfigList();
		Configurations result=null;
		int left=0,k=0;
		int right=Profiler.configList.size()-1;
		while(left<=right)
		{
			int mid=left+(right-left)/2;
			if(Predict(Profiler.configList.get(left),k++)<=deadline)
			{
				result=Profiler.configList.get(left);
				break;
			}
			else if(Predict(Profiler.configList.get(mid),k++)<=deadline)
			{
				result=Profiler.configList.get(mid);
				left++;
				right=mid-1;
			}
			else
				left=mid+1;
		}
		System.out.println("Total Profiled RAS: "+(k-1));
		if(result!=null)
		{
			System.out.println("Found Optimal Configuration");
			result.printConfig();
		}
		else
			System.out.println("No Configurations Found!");
		
	}
	static double Predict(Configurations configObj,int k)
	{
		inputSizes = new ArrayList<Double>();
		ProfilerDeployer profDepObj = new ProfilerDeployer();
		LogParser logParserObj = new LogParser();
		runCommand("rm -rf "+Settings.inputPathProfiler+"/tmpInput");
		runCommand("mkdir "+Settings.inputPathProfiler+"/tmpInput");
		copyInput();
		inputDirectory = new File(Settings.inputPathProfiler+"/tmpInput");

		for(int i=0;i<Settings.reprofileSize;i++)
		{
			//make profiling input directory 2x of current size
			if(i>0)		
				make2xInputSize(i);
			inputSizes.add((double)inputSize()/1024.0/1024.0);
			System.out.println("***Profiling Input Directory Size: "+inputSizes.get(i)+"MB");
			
			profDepObj.submitApps(configObj,k*Settings.reprofileSize);
			logParserObj.parseLog();
		}
		System.out.println("\n\n***RESULTS***\n\nApplicaiton Input Size is: "+applicationInputSize*1024+"MB");
		System.out.println("Profiling input sizes: ");
		for(int i=0;i<Settings.reprofileSize;i++)
		{
			System.out.println(inputSizes.get(i)+" MB");
		}
		
		//curve fit //input size vs completion time 
		CurveFitter.FitCurves(configObj);
		//approximate completion time for the whole input
		System.out.println("\n\n***RESULTS***\n\nApplicaiton Input Size is: "+applicationInputSize*1024+"MB");
		System.out.println("Profiling input sizes: ");
		for(int i=0;i<Settings.reprofileSize;i++)
		{
			System.out.println(inputSizes.get(i)+" MB");
		}
	
		double totalTime = configObj.getP1()*applicationInputSize*1024+configObj.getP2();
		System.out.println("Config coeffs:"+"-> P1: "+configObj.getP1()+" P2: "+configObj.getP2());
		System.out.println(" Predicted time for Config-> "+configObj.getCore()+" "+configObj.getMemory()+" "+configObj.getMaxCore()+" is: "+totalTime+" s");
		
		
		return totalTime;	
	}

	private static void copyInput() {
		inputDirectory = new File(Settings.inputPathProfiler);
		for (File file : inputDirectory.listFiles()) {
			if (file.isFile())
			{
				runCommand("cp "+file+" "+Settings.inputPathProfiler+"/tmpInput/"+file.getName());
			}
		}
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
			//else
				//length += inputSize();
		}
		System.out.println("File Length: "+length);
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
	public static void main(String[] args) {
		System.out.println("Please enter the input size of the whole application");
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		applicationInputSize = sc.nextLong();
		System.out.println("Please enter the Deadline of the application");
		deadline=sc.nextDouble();
		DSparkAlgoLatest();

	}
}
