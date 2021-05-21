package classification;

import java.io.Serializable;

public class SudokuExtractor implements Serializable {
	private static final long serialVersionUID = 4957704404415197164L;
	public final LogisticRegressionClassifier[] classifiers = new LogisticRegressionClassifier[9];
	
	public SudokuExtractor(int featureDimension) {
		for(int i = 0;i<classifiers.length;i++) {
			classifiers[i] = new LogisticRegressionClassifier(featureDimension);
		}
	}
	
	public int classify(double[] feature) {
		if(feature == null) return 0;
		double highestConfidence = 0;
		int bestClass = 0;
		for(int i = 0;i<classifiers.length;i++) {
			double confidence = classifiers[i].predict(feature);
			if(confidence >= highestConfidence) {
				highestConfidence = confidence;
				bestClass = i;
			}
		}
		return bestClass + 1;
	}
	
	public int[][] extractSudoku(double[][][] featureGrid){
		int[][] sudoku = new int[9][9];
		for(int i = 0;i<9;i++) {
			for(int j = 0;j<9;j++) {
				sudoku[i][j] = classify(featureGrid[i][j]);
			}
		}
		return sudoku;
	}
	
	public void trainClassifiers(double[] feature,int label,double stepSize) {
		if(feature == null) return;
		for(int i = 0;i<classifiers.length;i++) {
			classifiers[i].SGDStep(feature, label == i + 1 ? 1.0:0.0, stepSize);
		}
	}
	
	public void trainClassifiers(double[][][] featureGrid,int[][] sudoku,double stepSize) {
		for(int i = 0;i<9;i++) {
			for(int j = 0;j<9;j++) {
				trainClassifiers(featureGrid[i][j],sudoku[i][j],stepSize);
			}
		}
	}
}
