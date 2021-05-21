package solver;

public class SudokuSolver {
	private static final int EMPTY_CELL = 0;
	public static boolean solve(int[][] sudoku) {
		int i=0,j=0;
		boolean foundEmpty = false;
		for(int k = 0;k<81;k++) {
			i = k / 9;
			j = k % 9;
			if(sudoku[i][j] == EMPTY_CELL) {
				foundEmpty = true;
				break;
			}
		}
		if(!foundEmpty) return true;
		for(int digit = 1;digit<=9;digit++) {
			boolean digitIsLegal = true;
			
			for(int k = 0;k<9;k++) digitIsLegal &= sudoku[i][k] != digit;
			for(int k = 0;k<9;k++) digitIsLegal &= sudoku[k][j] != digit;
			
			int i0 = (i/3) * 3;
			int j0 = (j/3) * 3;
			for(int k = 0;k<3;k++) 
				for(int l = 0;l<3;l++) 
					digitIsLegal &= sudoku[i0 + k][j0 + l] != digit;
			
			if(digitIsLegal) {
				sudoku[i][j] = digit;
				if(solve(sudoku)) return true;
				else sudoku[i][j] = EMPTY_CELL;
			}
		}
		return false;
	}
}
