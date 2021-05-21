package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JPanel;

import classification.SudokuExtractor;
import detection.AABB;
import detection.GridFinder;
import detection.Preprocessor;
import homography.Matrix;
import solver.SudokuSolver;

public class Main {
	public static void main(String[] args) {
		BufferedImage image = null;
		final String IMAGE_PATH = args[0];

		try {
			image = ImageIO.read(new File(IMAGE_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		double[][] grayscale = Preprocessor.grayscale(image);
		int[][] binary1 = Preprocessor.adaptiveThreshold(grayscale, 7);
		int[][] binary = Preprocessor.dialate(binary1, 3);
		int[][] contour = Preprocessor.detectContours(binary);
				
		double[][] H = GridFinder.computeGridHomography(binary);

		double[] x = Matrix.multiply(H, new double[] {1,0,1});
		x[0] /= x[2];
		x[1] /= x[2];
		double[][] transformed = GridFinder.transform(500, 500, H, grayscale);
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
		
		int[][] predicted = extractor.extractSudoku(featureGrid);
		int[][] scanned = new int[predicted.length][predicted[0].length];
		for(int i = 0;i<9;i++) {
			for(int j = 0;j<9;j++) {
				System.out.print(predicted[i][j] + " ");
				scanned[i][j] = predicted[i][j];
			}
			System.out.println();
		}
		System.out.println();
		boolean solved = SudokuSolver.solve(predicted);
		
		if(solved) {
			for(int i = 0;i<9;i++) {
				System.out.print(predicted[i][0]);
				for(int j = 1;j<9;j++) {
					System.out.print(" " + predicted[i][j]);
				}
				System.out.println();
			}
		}else {
			System.out.println("Some digit must have been missclassified unless the sudoku in the image is unsolvable!");
			return;
		}
			
		
		class MyPanel extends JPanel{
			private static final long serialVersionUID = 2919707588530978770L;
			BufferedImage image;
			void setData(int[][] binary,int[][] contour) {
				image = new BufferedImage(contour.length,contour[0].length,BufferedImage.TYPE_3BYTE_BGR);
				Map<Integer,Integer> colors = new HashMap<>();
				colors.put(0, 0);
				for(int i = 0;i<contour.length;i++) {
					for(int j = 0;j<contour[0].length;j++) {
						int g = (int) (transformed[i][j] * 0x000000ff);
						image.setRGB(i, j,g | g << 8 | g << 16);
					}
				}
				Graphics g = image.getGraphics();
				
				g.setColor(Color.black);
				
				Font font = new Font("Monospaced",Font.PLAIN,40);
				g.setFont(font);
				for(int i = 0;i<9;i++) {
					for(int j = 0;j<9;j++) {
						if(scanned[i][j] == 0)
							g.drawString(Integer.toString(predicted[i][j]), j * 500 / 9 + 500/36, (i+1) * 500/9 - 500/36);
					}
				}
				BufferedImage grayscaleImage = new BufferedImage(grayscale.length,grayscale[0].length,BufferedImage.TYPE_3BYTE_BGR);
				for(int i = 0;i<grayscale.length;i++) {
					for(int j = 0;j<grayscale[0].length;j++) {
						int gray = (int) (grayscale[i][j] * 0x000000ff);
						grayscaleImage.setRGB(i, j,gray | gray << 8 | gray << 16);
					}
				}
				double[] position = {0,0,1};
				for(int i = 0;i<500;i++) {
					for(int j = 0;j<500;j++) {
						position[1] = i/500.0;
						position[0] = j/500.0;
						double[] transformed = Matrix.multiply(H, position);
						transformed[0] /= transformed[2];
						transformed[1] /= transformed[2];
						grayscaleImage.setRGB((int) transformed[0], (int) transformed[1], image.getRGB(i, j));
					}
				}
				image = grayscaleImage;
			}
			@Override
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, null);
			}
		}
		JFrame frame = new JFrame();
		MyPanel panel = new MyPanel();
		panel.setData(GridFinder.transform(500, 500, H, binary1),GridFinder.transform(500, 500, H, contour));	
		panel.setSize(image.getWidth() + 100,image.getHeight() + 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setSize(image.getWidth() + 100,image.getHeight() + 100);
		frame.setResizable(false);
		frame.setVisible(true);

		while(true) {
			frame.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}