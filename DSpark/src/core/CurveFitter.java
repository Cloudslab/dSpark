package core;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class CurveFitter {

	public static void FitCurves() {

		for(int j=0;j<Profiler.configList.size();j++)
		{
			final WeightedObservedPoints obs = new WeightedObservedPoints();
			// Collect data.
			obs.add(DSpark.inputSizes[0], Profiler.configList.get(j).getCompletionTime(0));
			obs.add(DSpark.inputSizes[1], Profiler.configList.get(j).getCompletionTime(1));
			obs.add(DSpark.inputSizes[2], Profiler.configList.get(j).getCompletionTime(2));
			
			// Instantiate a first-degree polynomial fitter.
			PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

			// Retrieve fitted parameters (coefficients of the polynomial function).
			final double[] coeff = fitter.fit(obs.toList());
			Profiler.configList.get(j).setP1(coeff[0]);
			Profiler.configList.get(j).setP2(coeff[1]);

		}
	}

}
