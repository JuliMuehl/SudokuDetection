package training;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

import classification.SudokuExtractor;
import detection.AABB;
import detection.GridFinder;
import detection.Preprocessor;

public class Training {
	public static void trainWithFile(File imageFile,int[][] sudoku,int numberIterations,double stepSize) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		double[][] grayscale = Preprocessor.grayscale(image);
		int[][] binary1 = Preprocessor.adaptiveThreshold(grayscale, 7);
		int[][] binary = Preprocessor.dialate(binary1, 3);
		int[][] contour = Preprocessor.detectContours(binary);
		
				
		double[][] H = GridFinder.computeGridHomography(binary);

		AABB[][] boxGrid = GridFinder.boundingBoxGrid(GridFinder.transform(500,500,H,contour), 20);
		double[][][] featureGrid = GridFinder.generateFeatureGrid(GridFinder.transform(500, 500, H, binary1), boxGrid);
		SudokuExtractor extractor = new SudokuExtractor((500/9) * (500/9));
		
		
		try {
			FileInputStream fin = new FileInputStream("extractor.ser");
			ObjectInputStream objin = new ObjectInputStream(fin);
			extractor = (SudokuExtractor) objin.readObject();
			objin.close();
		}catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		for(int iter = 0;iter<numberIterations;iter++) {
			 
			extractor.trainClassifiers(featureGrid, sudoku, stepSize);
		}
		
		try {
			FileOutputStream fout = new FileOutputStream("extractor.ser");
			ObjectOutputStream objout = new ObjectOutputStream(fout);
			objout.writeObject(extractor);
			objout.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		int[][] sudoku = {
				{0,9,8,0,0,0,0,4,0},
				{0,0,0,6,0,2,0,5,3},
				{0,6,3,9,1,0,2,0,8},
				{0,8,2,7,0,0,0,0,5},
				{6,0,0,0,0,0,0,0,2},
				{9,0,0,0,0,5,6,8,0},
				{7,0,6,0,5,8,3,2,0},
				{3,1,0,2,0,7,0,0,0},
				{0,2,0,0,0,0,5,1,0}
		};
		File folder = new File("sudokus/training1");
		File[] folderFiles = folder.listFiles();
		for(int i = 0;i<10;i++) {
			System.out.println(i);
			for(File imageFile : folderFiles) {
				System.out.println(imageFile.getAbsolutePath());
				trainWithFile(imageFile,sudoku,200,.01);
			}
		}
		
		sudoku = new int[][] {
				{4,1,0,8,3,0,7,0,5},
				{7,8,0,0,0,0,0,9,0},
				{0,0,0,1,0,5,4,2,0},
				{5,7,0,6,0,0,2,0,0},
				{0,0,1,0,0,0,5,0,0},
				{0,0,8,0,0,2,0,7,1},
				{0,3,4,5,0,6,0,0,0},
				{0,5,0,0,0,0,0,3,2},
				{1,0,6,0,2,7,0,5,4}};
		folder = new File("sudokus/training2");
		folderFiles = folder.listFiles();
		for(int i = 0;i<10;i++) {
			System.out.println(i);
			for(File imageFile : folderFiles) {
				System.out.println(imageFile.getAbsolutePath());
				trainWithFile(imageFile,sudoku,200,.1);
			}
		}
		
	}
}
