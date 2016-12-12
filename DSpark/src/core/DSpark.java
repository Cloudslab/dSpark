package core;

import java.util.Collections;
import java.util.Scanner;


public class DSpark {

	static int deadline;
	static double profileTime;
	static double deadlineThreshold=1.50;
	static int inputRatio=10;
	
	
	//Algorithm for Deadline-aware spark applications with optimized cluster usage 
	static void DSparkAlgo()
	{
		Configurations bestConfig=new Configurations();
		//profile the application with the given profiler inputs
		//generate completion time of the application with each configuration
		Profile profileObj = new Profile();
		profileObj.profile();
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
			profileTime+=Profiler.configList.get(i).getCompletionTime();
	
		}
		System.out.println("Profiling Time: "+profileTime);
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
		
	}
	
	
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
	
		System.out.println("Please enter the application deadline in seconds");
		deadline=sc.nextInt();
		deadline/=inputRatio;
		deadline*=1000;
		deadline+=268073;
		System.out.println("Deadline: "+deadline);
		DSparkAlgo();
		
	}
}
