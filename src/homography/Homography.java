package homography;

public class Homography {
	public static double[][] findHomography(double[][] pointsA,double[][] pointsB){
		double[][] P = new double[2*pointsA.length][];
		for(int i = 0;i<pointsA.length;i++) {
			double x = pointsA[i][0],y = pointsA[i][1];
			double u = pointsB[i][0],v = pointsB[i][1]; 
			P[2*i] = new double[] {-x,-y,-1,0,0,0,u*x,u*y,u};
			P[2*i+1] = new double[] {0,0,0,-x,-y,-1,v*x,v*y,v};
		}
		double[][] PTP = Matrix.square(P);
		double[][] PTPinv = Matrix.inverse(PTP);
		double[] h = Matrix.approximateDominantEigenvector(PTPinv);
		double[][] H = new double[3][3];
		for(int i = 0;i<3;i++) {
			for(int j = 0;j<3;j++) {
				H[i][j] = h[i*3+j];
			}
		}
		return H;
	}
	
}
