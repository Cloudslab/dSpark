package core;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class CurveFitter {

	public static void FitCurves(Configurations configObj) {

		final WeightedObservedPoints obs = new WeightedObservedPoints();
		// Collect data.

		for(int i=0,j=0;i<DSpark.inputSizes.size();i++)
			for(int k=0;k<Settings.reprofileSize;k++,j++)
			{
				obs.add(DSpark.inputSizes.get(i), configObj.getCompletionTimei(j)/1000);
			}
		
		// Instantiate a first-degree polynomial fitter.
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

		// Retrieve fitted parameters (coefficients of the polynomial function).
		final double[] coeff = fitter.fit(obs.toList());
		configObj.setP2(coeff[0]);
		configObj.setP1(coeff[1]);
		
	}
}