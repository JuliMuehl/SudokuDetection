package classification;

import java.io.Serializable;

public class LogisticRegressionClassifier implements Serializable{
	private static final long serialVersionUID = 543496580074293905L;
	private double[] w;
	private double b;
	private static double sigmoid(double x) {
		return 1/(1+Math.exp(-x));
	}
	public LogisticRegressionClassifier(int dimension) {
		w = new double[dimension];
		for(int i = 0;i<w.length;i++)
			w[i] = 2*Math.random() - 1;
		b = 2*Math.random()-1;
	}
	public LogisticRegressionClassifier(String serializedWeights) {
		String[] stringData = serializedWeights.split(";");
		String[] wstr = stringData[0].split(",");
		w = new double[wstr.length];
		for(int i = 0;i<wstr.length;i++) 
			w[i] = Double.parseDouble(wstr[i]);
		b = Double.parseDouble(stringData[1]);
	}
	public double predict(double[] x) {
		double z = b;
		for(int i = 0;i<x.length;i++) {
			z += w[i] * x[i] ;
		}
		return sigmoid(z);
	}
	public void SGDStep(double[] x,double y,double stepSize) {
		double p = predict(x);
		double error = y-p;
		for(int i = 0;i<w.length;i++) {
			w[i] += stepSize * error * x[i];
		}
		b += stepSize * error;
	}
}
