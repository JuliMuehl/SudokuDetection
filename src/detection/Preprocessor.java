package detection;

import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Preprocessor {
	public static final int[][] NEIGHBOURHOOD8 = {{1,0},{0,1},{-1,0},{0,-1},{-1,-1},{1,-1},{1,1},{-1,1}};
	
	public static double[][] grayscale(BufferedImage image){
		double[][] image_data = new double[image.getWidth()][image.getHeight()];
		for(int i = 0;i<image.getWidth();i++) {
			for(int j = 0;j<image.getHeight();j++) {
				int bgr = image.getRGB(i, j);
				double b = (double) (bgr & 0xff) / 0xff;
				double g = (double) (bgr >> 8 & 0xff) / 0xff;
				double r = (double) (bgr >> 16 & 0xff) / 0xff;
				double grayscale = 1.0 / 3.0 * (b + g + r);
				image_data[i][j] = grayscale;
			}
		}
		return image_data;
	}
	
	public static double averageBrightness(double[][] grayscale,int x0,int y0,int width,int height) {
		double average = 0;
		int iMin = Math.max(x0-width/2,0),iMax = Math.min(x0+width/2,grayscale.length);
		int jMin = Math.max(y0-height/2,0),jMax = Math.min(y0+height/2,grayscale[0].length);
		for(int i = iMin;i<iMax;i++) {
			for(int j =jMin ;j<jMax;j++) { 
				average += grayscale[i][j];
			}
		}
		average /= width * height;
		return average;
	}
	public static int[][] adaptiveThreshold(double[][] grayscale,int windowSize) {
		int[][] result = new int[grayscale.length][grayscale[0].length];
		for(int i = 0;i<grayscale.length;i++) {
			for(int j = 0;j<grayscale[i].length;j++) {
				double average = averageBrightness(grayscale,i,j,windowSize,windowSize);
				if(grayscale[i][j] <= average)
					result[i][j] = 1;
			}
		}
		return result;
	}
	public static int[][] dialate(int[][] binaryImage,int windowSize){
		int[][] result = new int[binaryImage.length][binaryImage[0].length];
		for(int i = windowSize/2;i<binaryImage.length-windowSize/2;i++) {
			for(int j = windowSize/2;j<binaryImage[0].length-windowSize/2;j++) {
				result[i][j] = binaryImage[i][j];
				for(int dx = -windowSize/2;dx<windowSize/2;dx++) {
					for(int dy = -windowSize/2;dy<windowSize/2;dy++) {
						if(binaryImage[i+dx][j+dy] == 1) {
							result[i][j] = 1;
						}
					}
				}
			}
		}
		return result;
	}
	
	public static int[][] erode(int[][] binaryImage,int windowSize){
		int[][] result = new int[binaryImage.length][binaryImage[0].length];
		for(int i = windowSize/2;i<result.length-windowSize/2;i++) {
			for(int j = windowSize/2;j<binaryImage[0].length-windowSize/2;j++) {
				if(binaryImage[i][j] == 1) {
					result[i][j] = 1;
					for(int dx = -windowSize/2;dx<windowSize/2;dx++) {
						for(int dy=-windowSize/2;dy<windowSize/2;dy++) {
							if(binaryImage[i+dx][j+dy] != 1) {
								result[i][j] = 0;
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private static boolean isPerimeter(int x,int y,int[][] binaryImage,int[][] contourImage) {
		if(x < 1 || x >= binaryImage.length-1 || y < 1 || y >= binaryImage[0].length-1 || binaryImage[x][y] == 0 || contourImage[x][y] != 0) return false;
		boolean result = false;
		for(int[] delta:NEIGHBOURHOOD8) {
			if(binaryImage[x+delta[0]][y+delta[1]] == 0) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	private static void dfs(int x,int y,int[][] binaryImage,int[][] contourImage,int color) {
		Stack<int[]> points = new Stack<>();
		points.add(new int[] {x,y});
		while(!points.isEmpty()) {
			int[] point = points.pop();
			x = point[0];
			y = point[1];
			if(!isPerimeter(x,y,binaryImage,contourImage)) continue;
			contourImage[x][y] = color;
			for(int[] delta:NEIGHBOURHOOD8) {
				points.push(new int[]{x+delta[0],y+delta[1]});
			}
		}
	}
	
	public static int[][] detectContours(int[][] binaryImage) {
		int[][] contourImage = new int[binaryImage.length][binaryImage[0].length];
		int color = 1;
		for(int i = 0;i<binaryImage.length;i++) {
			for(int j = 0;j<binaryImage[0].length;j++) {
				if(binaryImage[i][j] == 1) {
					dfs(i,j,binaryImage,contourImage,color);
					color++;
				}
			}
		}
		return contourImage;
	}
	public static int contourLength(int[][] contourImage,int contour) {
		int length = 0;
		for(int i = 0;i<contourImage.length;i++) {
			for(int j = 0;j<contourImage.length;j++) {
				if(contourImage[i][j] == contour) {
					length++;
				}
			}
		}
		return length;
	}
	public static Map<Integer,Integer> contourAreas(int[][] contourImage){
		Map<Integer,Integer> areas = new HashMap<>();
		Set<Integer> inside = new HashSet<>();
		for(int i = 0;i<contourImage.length;i++) {
			inside.clear();
			for(int j = 0;j<contourImage[i].length;j++) {
				if(inside.contains(contourImage[i][j])) inside.remove(contourImage[i][j]);
				else if(contourImage[i][j] >= 2) inside.add(contourImage[i][j]);
				for(int contour:inside) {
					Integer area = areas.get(contour);
					if(area == null) areas.put(contour,0);
					else areas.put(contour, areas.get(contour) + 1);
				}
			}
		}
		return areas;
	}
	public static Map<Integer,Integer> contourLengths(int[][] contourImage){
		Map<Integer,Integer> lengths = new HashMap<>();
		for(int i = 0;i<contourImage.length;i++) {
			for(int j = 0;j<contourImage[i].length;j++) {
				if(contourImage[i][j] != 0) {
					if(!lengths.containsKey(contourImage[i][j])) lengths.put(contourImage[i][j], 0);
					lengths.put(contourImage[i][j],lengths.get(contourImage[i][j])+1);
				}
			}
		}
		return lengths;
	}
	public static Map<Integer,AABB> contourBoundingBoxes(int[][] contourImage){
		Map<Integer,AABB> boundingBoxes = new HashMap<>();
		for(int i = 0;i<contourImage.length;i++) {
			for(int j = 0;j<contourImage[0].length;j++) {
				if(contourImage[i][j] != 0) {
					if(!boundingBoxes.containsKey(contourImage[i][j])) boundingBoxes.put(contourImage[i][j], new AABB(i,j,i,j));
					AABB box = boundingBoxes.get(contourImage[i][j]);
					box.xmin = Math.min(box.xmin, i);
					box.ymin = Math.min(box.ymin, j);
					box.xmax = Math.max(box.xmax, i);
					box.ymax = Math.max(box.ymax, j);
				}
			}
		}
		return boundingBoxes;
	}
}
