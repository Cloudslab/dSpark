package core;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class CurveFitter {

	public static void FitCurves() {

		for(int j=0;j<Profiler.configList.size();j++)
		{
			final WeightedObservedPoints obs = new WeightedObservedPoints();
			// Collect data.
			for(int i=0;i<DSpark.inputSizes.size();i++)
			obs.add(DSpark.inputSizes.get(i), Profiler.configList.get(j).getCompletionTimei(i)/1000);
			
			// Instantiate a first-degree polynomial fitter.
			PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

			// Retrieve fitted parameters (coefficients of the polynomial function).
			final double[] coeff = fitter.fit(obs.toList());
			Profiler.configList.get(j).setP2(coeff[0]);
			Profiler.configList.get(j).setP1(coeff[1]);

		}
	}
}