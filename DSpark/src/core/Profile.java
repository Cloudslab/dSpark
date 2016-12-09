package core;

import java.util.ArrayList;

public class Profile {


	void profile() {

		Profiler.configList = new ArrayList<Configurations>();
		
		
		//Load Settings for Profiler
		SettingsLoader.loadSettings();

		Settings.printSettings();
		ConfigurationGenerator configGenObj = new ConfigurationGenerator();
		
		//Prepare Input Using MicroBenachMarking Suits

		//For Real Application Scenario, Do something to get a portion of the original input
		//call ProfilerInputManager

		//Generate configurations of application for profiler
		configGenObj.generateAppConfig();
		configGenObj.generateSparkSubmitList();

		//start profiling of application with generated configurations
		//ProfilerDeployer profileDeployerObj = new ProfilerDeployer();
		//profileDeployerObj.submitApps();

		LogParser logParserObj = new LogParser();
		logParserObj.parseLog();

	}
}
