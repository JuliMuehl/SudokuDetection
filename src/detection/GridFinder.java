package detection;

import java.util.Map;

import homography.Homography;
import homography.Matrix;

public class GridFinder {
	private static double[] getFeature(int featureSize,AABB box, int[][] binaryImage) {
		if(box == null) return null;
		int dx = (featureSize - box.getWidth())/2;
		int dy = (featureSize - box.getHeight())/2;
		double[] feature = new double[featureSize * featureSize];
		for(int i = 0;i<box.getWidth();i++) {
			for(int j = 0;j<box.getHeight();j++) {
				feature[(i+dx)*featureSize + (j + dy)] = binaryImage[box.xmin + i][box.ymin + j];
			}
		}
		return feature;
	}
	
	public static double[][][] generateFeatureGrid(int[][] binaryImage,AABB[][] boxGrid){
		int size = binaryImage.length;
		int featureSize = size/9;
		double[][][] featureGrid = new double[9][9][];
		for(int i = 0;i<9;i++) {
			for(int j = 0;j<9;j++) {
				featureGrid[i][j] = getFeature(featureSize,boxGrid[i][j],binaryImage);
			}
		}
		return featureGrid;
	}
	
	

	private static AABB getBoundingBox(Map<Integer,AABB> boundingBoxes,int x,int y,int windowSize,int[][] contourImage){
		x -= windowSize/2;
		y -= windowSize/2;
		int boxSize = contourImage.length/9;
		AABB result = null;
		for(int i = 0;i<windowSize;i++) {
			for(int j = 0;j<windowSize;j++) {
				if(contourImage[x + i][y + j]!=0) {
					AABB box = boundingBoxes.get(contourImage[x + i][y + j]);
					if(box.getWidth() < boxSize && box.getHeight() < boxSize ) {
						if(result == null) result = box;
						else if(box.getArea() > result.getArea()) {
							result = box;
						}
					}
				}
			}
		}
		return result;
	}
	
	public static AABB[][] boundingBoxGrid(int[][] contour,int windowSize){
		Map<Integer, AABB> boundingBoxes = Preprocessor.contourBoundingBoxes(contour);
		AABB[][] boxGrid = new AABB[9][9];
		int size = contour.length;
		for(int i = 0;i<9;i++) {
			for(int j = 0;j<9;j++) {
				int x = (int) ((1.0/18.0 + i/9.0) * size);
				int y = (int) ((1.0/18.0 + j/9.0) * size);
				boxGrid[j][i] = getBoundingBox(boundingBoxes,x,y,windowSize,contour);
			}
		}
		return boxGrid;
	}
	
	public static double[][] transform(int width,int height,double[][] H,double[][] image){
		double[][] transformed = new double[width][height];
		double[] position = {0,0,1};
		for(int i = 0;i<width;i++) {
			for(int j = 0;j<height;j++) {
				position[0] = (double) j/width;
				position[1] = (double) i/height;
				position[2] = 1;
				position = Matrix.multiply(H, position);
				position[0] /= position[2];
				position[1] /= position[2];
				position[0] = Math.max(0, position[0]);
				position[0] = Math.min(image.length-1, position[0]);
				position[1] = Math.max(0, position[1]);
				position[1] = Math.min(image[0].length-1, position[1]);
				transformed[i][j] = image[(int)position[0]][(int)position[1]]; 
			}
		}
		return transformed;
	}
	
	public static int[][] transform(int width,int height,double[][] H,int[][] image){
		int[][] transformed = new int[width][height];
		double[] position = {0,0,1};
		for(int i = 0;i<width;i++) {
			for(int j = 0;j<height;j++) {
				position[0] = (double) j/width;
				position[1] = (double) i/height;
				position[2] = 1;
				position = Matrix.multiply(H, position);
				position[0] /= position[2];
				position[1] /= position[2];
				position[0] = Math.max(0, position[0]);
				position[0] = Math.min(image.length-1, position[0]);
				position[1] = Math.max(0, position[1]);
				position[1] = Math.min(image[0].length-1, position[1]);
				transformed[i][j] = image[(int)position[0]][(int)position[1]]; 
			}
		}
		return transformed;
	}

	
	public static double[][] computeGridHomography(int[][] binaryImage){
		int[][] contourImage = Preprocessor.detectContours(binaryImage);
		int bestContour = 0;
		int bestLength = 0;
		Map<Integer,Integer> lenghts = Preprocessor.contourLengths(contourImage);
		for(Map.Entry<Integer,Integer> entry:lenghts.entrySet()) {
			int contour = entry.getKey();
			int length = entry.getValue();
			if(length >= bestLength) {
				bestLength = length;
				bestContour = contour;
			}
		}
		int topLeftX = contourImage.length-1,topLeftY = contourImage[0].length-1;
		int bottomRightX = 0,bottomRightY = 0;
		int bottomLeftX = contourImage.length-1,bottomLeftY = 0;
		int topRightX = 0,topRightY = contourImage[0].length-1;
		for(int i = 0;i<contourImage.length;i++) {
			for(int j = 0;j<contourImage[i].length;j++) {
				if(contourImage[i][j] == bestContour) {
					if(i + j < topLeftX + topLeftY) {
						topLeftX = i;
						topLeftY = j;
					}
					if(bottomRightX + bottomRightY < i + j) {
						bottomRightX = i;
						bottomRightY = j;
					}
					if(topRightX - topRightY <= i - j) {
						topRightX = i;
						topRightY = j;
					}
					if(bottomLeftY - bottomLeftX <= j - i) {
						bottomLeftX = i;
						bottomLeftY = j;
					}
				}
			}
		}
		double[][] pointsA = {{topLeftX,topLeftY},{topRightX,topRightY},{bottomLeftX,bottomLeftY},{bottomRightX,bottomRightY}};
		double[][] pointsB = {{0,0},{0,1},{1,0},{1,1}};
		double[][] H = Homography.findHomography(pointsB, pointsA);
		return H;
	}
}
