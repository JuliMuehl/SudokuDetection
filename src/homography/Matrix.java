package homography;

import java.util.Arrays;

public class Matrix {
	
	private static void swapRows(double[][] A,int i,int j) {
		double[] tmp = A[i];
		A[i] = A[j];
		A[j] = tmp;
	}
	
	public static double[][] copy(double[][] A){
		double[][] Acopy = new double[A.length][A[0].length];
		for(int i = 0;i<A.length;i++) {
			for(int j = 0;j<A[0].length;j++) {
				Acopy[i][j] = A[i][j];
			}
		}
		return Acopy;
	}
	
	public static void print(double[][] A) {
		System.out.println(Arrays.deepToString(A));
	}
	
	private static double TOLLERANCE = 1e-20;
	
	private static boolean closeToZero(double x) {
		return -TOLLERANCE <= x && x <= TOLLERANCE;
	}
	
	public static double[][] inverse(double[][] A) {
		A = copy(A);
		final int n = A.length;
		double[][] result = new double[n][n];
		for(int i = 0;i<n;i++) {
			result[i][i] = 1;
		}
		for(int i = 0;i<n;i++) {
			if(closeToZero(A[i][i])) {
				boolean foundRow = false;
				for(int j = 0;j<n;j++) {
					if(!closeToZero(A[i][j])) {
						swapRows(A,i,j);
						swapRows(result,i,j);
						foundRow = true;
						break;
					}
				}
				if(!foundRow) throw new RuntimeException("Singular matrix");	
			}
			for(int j = 0;j<n;j++) {
				if(i != j) {
					double c = A[j][i]/A[i][i];
					for(int k = 0;k<n;k++) {
						A[j][k] -= c * A[i][k];
						result[j][k] -= c * result[i][k];
					}
				}
			}
		}
		for(int i = n-1;i>=0;i--) {
			for(int j = 0;j<n;j++) {
				result[i][j] /= A[i][i];
			}
		}
		return result;
	}
	
	public static double[][] multiply(double[][] A,double[][] B){
		if(A[0].length!= B.length) throw new RuntimeException("Wrong dimensions for matrix product");
		double[][] result = new double[A.length][B[0].length];
		for(int i = 0;i<A.length;i++) {
			for(int j = 0;j<B[0].length;j++) {
				for(int k = 0;k<B.length;k++) {
					result[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return result;
	}
	
	public static double[] multiply(double[][] A,double[] x){
		if(A[0].length!= x.length) throw new RuntimeException("Wrong dimensions for matrix product");
		double[] result = new double[A.length];
		for(int i = 0;i<A.length;i++) {
			for(int j = 0;j<x.length;j++) {
				result[i] += A[i][j] * x[j];
			}
		}
		return result;
	}
	
	public static double[][] square(double[][] A){
		double[][] result = new double[A[0].length][A[0].length];
		for(int i = 0;i<A[0].length;i++){
			for(int j = 0;j<A[0].length;j++) {
				for(int k = 0;k<A.length;k++) {
					result[i][j] += A[k][i] * A[k][j];
				}
			}
		}
		return result;
	}
	
	public static double[] approximateDominantEigenvector(double[][] A,int NUM_ITERATIONS) {
		final int n = A.length;
		double[] v = new double[n];
		for(int i = 0;i<n;i++) {
			v[i] = Math.random();
		}
		double[] w = new double[n];
		for(int iteration = 1;iteration <= NUM_ITERATIONS;iteration++) {
			for(int i = 0;i<n;i++) {
				w[i] = 0;
				for(int j = 0;j<n;j++) {
					w[i] += A[i][j] * v[j];
				}
				
			}
			double norm = 0;
			for(int i = 0;i<n;i++) {
				norm += w[i] * w[i];
			}
			norm = Math.sqrt(norm);
			for(int i = 0;i<n;i++) {
				w[i] /= norm;
			}
			double[] tmp = v;
			v = w;
			w = tmp;
		}
		return v;
	}
	
	private static int DEFAULT_NUM_ITERATIONS = 10;
	public static double[] approximateDominantEigenvector(double[][] A) {
		return approximateDominantEigenvector(A,DEFAULT_NUM_ITERATIONS);
	}
}
