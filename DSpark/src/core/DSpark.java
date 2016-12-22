package core;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;


public class DSpark {

	static int deadline;
	static double profileTime;
	static double deadlineThreshold=1.50;
	static int inputRatio=10;
	static File inputDirectory;
	static double[] inputSizes = new double[3];
	static long applicationInputSize;
	//Algorithm for Deadline-aware spark applications with optimized cluster usage 
	static void DSparkAlgo()
	{
		
		ProfilerDeployer profDepObj = new ProfilerDeployer();
		//Configurations bestConfig=new Configurations();
		LogParser logParserObj = new LogParser();
		//profile the application with the given profiler inputs
		//generate completion time of the application with each configuration
		Profile profileObj = new Profile();
		profileObj.profile();
		inputDirectory = new File(Settings.inputPathProfiler);
		//Profiler.printConfigList();
		
		//sort the application according to the completion times
		Collections.sort(Profiler.configList, new Configurations());
		
		System.out.println("\n\n\n******All Configs******");
		//print all the generated configs depending on profiling level 
		Profiler.printConfigList();
		
		//calculate the total time needed for profiling
		profileTime=0;
		for(int i=0;i<Profiler.configList.size();i++)
		{
			profileTime+=Profiler.configList.get(i).getCompletionTimei(0);
	
		}
		System.out.println("Profiling Time: "+profileTime);
		int configSizeAtStart=Profiler.configList.size();
		//Remove all the configs except the top 3
		if(Profiler.configList.size()>3)
		{
			while(Profiler.configList.size()>3)
				Profiler.configList.remove(3);
		}
		System.out.println("\n\n*******Top 3 Configs*******\n\n");
		Profiler.printConfigList();
		//Now we only have the best 3 configs in terms of the completion time of profiling
		
		//Run profiling for 3 different size of inputs for each config
		inputSizes[0]=(double)inputSize()/1024.0/1024.0;;
		for(int i=1;i<3;i++)
		{
			
			//make profiling input directory 2x of current size
			make2xInputSize(i);
			inputSizes[i]=((double)inputSize()/1024.0/1024.0);
			System.out.println("***Re Profiling Input Directory Size: "+inputSizes[i]+"MB");
	
			//Profile.configGenObj.generateSparkSubmitList();
			//Profiler.printConfigList();
			profDepObj.submitApps(configSizeAtStart);
			configSizeAtStart+=3;
			//Parse the logs for profiling runs of this step
			logParserObj.parseLog();
		}
		
	
		//print the top 3 configs with  reprofiling outputs
		Profiler.printConfigList();
		//curve fit //input size vs completion time 
		
		CurveFitter.FitCurves();
		//approximate completion time for the whole input
		System.out.println("Applicaiton Input Size is: "+applicationInputSize*1024+"MB");
		System.out.println("Profiling input sizes: ");
				for(int i=0;i<3;i++)
				{
					System.out.println(inputSizes[i]+" MB");
				}
		for(int i=0;i<Profiler.configList.size();i++)
		{
			double totalTime = Profiler.configList.get(i).getP1()*applicationInputSize*1024+Profiler.configList.get(i).getP2();
			System.out.println("Config "+(i+1)+"-> P1: "+Profiler.configList.get(i).getP1()+" P2: "+Profiler.configList.get(i).getP2());
			System.out.println(" Predicted time for Config-> "+Profiler.configList.get(i).getCore()+" "+Profiler.configList.get(i).getMemory()+" "+Profiler.configList.get(i).getMaxCore()+" is: "+totalTime+" s");
		}
		// choose the best config (constraints: deadline, min resource usages)
		
		/*
		//remove the application configurations that do not meet the deadline
		
		for(int i=0;i<Profiler.configList.size();i++)
		{
			double totalTime = (Profiler.configList.get(i).getCompletionTime()*deadlineThreshold+profileTime);
			System.out.println("Total Time: "+totalTime);
			if(totalTime>deadline)
			{
				Profiler.configList.remove(i);
				i--;
				System.out.println("*Config Removed*");
			}
		}
		//print config after config removal
		System.out.println("\n\n\n*****after removing configs that do not meet deadline: "+Profiler.configList.size());
		Profiler.printConfigList();
		//Now we only have the configurations that meet the deadline
		//select the one which uses least amount of resources
		bestConfig=Profiler.configList.get(0);
		for(int i=1;i<Profiler.configList.size();i++)
		{
			if(Profiler.configList.get(i).getTotalCores()<bestConfig.getTotalCores())
			{
				bestConfig=Profiler.configList.get(i);
			}
			else if(Profiler.configList.get(i).getTotalCores()==bestConfig.getTotalCores())
			{
				if(Profiler.configList.get(i).getTotalMemory()<bestConfig.getTotalMemory())
				{
					bestConfig=Profiler.configList.get(i);
				}
				else if(Profiler.configList.get(i).getTotalMemory()==bestConfig.getTotalMemory())
				{
					if(Profiler.configList.get(i).getCompletionTime()<bestConfig.getCompletionTime())
					{
						bestConfig=Profiler.configList.get(i);
					}
				}
			}
		}
		System.out.println("\n\n\n****Best Configuration: ");
		bestConfig.printConfig();
		*/
		
	}
	
	private static void make2xInputSize(int i) {
		
		
		for (File file : inputDirectory.listFiles()) {
	        if (file.isFile())
	        	//System.out.println("cp "+file+" "+file+"r");
	            runCommand("cp "+file+" "+file+i);
	       
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
