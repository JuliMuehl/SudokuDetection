# SudokuDetection
A java program that detects sudokus from images and solves them.The program doesn't require OpenCV or any other external libraries. <br>

# Input / Output:
<img src="https://i.postimg.cc/cLjz4QHq/sudoku4.jpg">
<img src="https://i.postimg.cc/8C7VZpFG/Solved-Sudoku.png">



# How it works:

* Compute binary image using adaptive thresholding
* Perform contour detection and identify the largest contour in the Image
* Compute homography matrix from the corners of the largest contour (using the power method)
* Use the homography matrix to transform the sudoku into a square image
* Identify and refine the grid cells of the sudoku (using contour information)
* Classify the digits inside the cells using multiple Logistic Regression classifiers  
* Render the results into the original image using the homography matrix


# Usage

The sudokus i used are all generated using this website: <a src="https://1sudoku.com/print-sudoku"> https://1sudoku.com/print-sudoku </a>. <br>
If you want to solve sudokus with a different font you will probably need to retrain the classifier by changing the files in the sudoku/training<1/2> directories and adjusting the arrays in training.Training.java. You should also make sure that the images have a resolution of about 500 X 500 pixels otherwise the contour detection may take very long.




