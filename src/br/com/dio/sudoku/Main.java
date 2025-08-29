package br.com.dio.sudoku;

import java.util.*;

public class Main {
    private static final int SIZE = 9;
    private static final int EMPTY = 0;
    private int[][] board = new int[SIZE][SIZE];
    private Random rand = new Random();

    public static void main(String[] args) {
        Main game = new Main();
        game.startCLI();
    }

    private void startCLI() {
        System.out.println("=== Sudoku Game ===");
        System.out.println("Gerando um tabuleiro resolvido...");
        generateSolvedBoard();
        int[][] puzzle = copyBoard(board);
        int removeCount = 45; // dificuldade média; aumente para mais difícil
        removeCells(puzzle, removeCount);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nTabuleiro (0 = vazia):");
            printBoard(puzzle);
            System.out.println("Opções: (1) Inserir valor  (2) Pedir solução  (3) Novo jogo  (4) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine().trim();
            if (opt.equals("1")) {
                try {
                    System.out.print("Linha(1-9): ");
                    int r = Integer.parseInt(sc.nextLine().trim()) - 1;
                    System.out.print("Coluna(1-9): ");
                    int c = Integer.parseInt(sc.nextLine().trim()) - 1;
                    System.out.print("Valor(1-9): ");
                    int v = Integer.parseInt(sc.nextLine().trim());
                    if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || v < 1 || v > 9) {
                        System.out.println("Entrada inválida.");
                        continue;
                    }
                    if (puzzle[r][c] != EMPTY) {
                        System.out.println("Essa célula já está preenchida.");
                        continue;
                    }
                    if (isSafe(puzzle, r, c, v)) {
                        puzzle[r][c] = v;
                        System.out.println("Valor inserido.");
                        if (isSolved(puzzle)) {
                            System.out.println("Parabéns! Você resolveu o Sudoku!\nTabuleiro final:");
                            printBoard(puzzle);
                            System.out.println("Deseja jogar novamente? (s/n)");
                            String yn = sc.nextLine().trim().toLowerCase();
                            if (yn.equals("s") || yn.equals("sim")) {
                                resetPuzzle(puzzle);
                                continue;
                            } else break;
                        }
                    } else {
                        System.out.println("Movimento inválido: quebra as regras do Sudoku.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Entrada inválida.");
                }
            } else if (opt.equals("2")) {
                System.out.println("Solução (tabuleiro resolvido):");
                printBoard(board);
            } else if (opt.equals("3")) {
                generateSolvedBoard();
                puzzle = copyBoard(board);
                removeCells(puzzle, removeCount);
                System.out.println("Novo jogo gerado.");
            } else if (opt.equals("4")) {
                System.out.println("Saindo. Até a próxima!");
                break;
            } else {
                System.out.println("Opção inválida.");
            }
        }
        sc.close();
    }

    private void resetPuzzle(int[][] puzzle) {
        // reset by re-generating a new puzzle based on existing solution
        puzzle = copyBoard(board);
        removeCells(puzzle, 45);
    }

    private boolean isSolved(int[][] b) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (b[r][c] == EMPTY) return false;
        // simple check: each position equals solved board
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (b[r][c] != board[r][c]) return false;
        return true;
    }

    private void generateSolvedBoard() {
        board = new int[SIZE][SIZE];
        fillBoard(board, 0, 0);
    }

    private boolean fillBoard(int[][] b, int row, int col) {
        if (row == SIZE) return true;
        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col + 1) % SIZE;

        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) nums.add(i);
        Collections.shuffle(nums, rand);

        for (int num : nums) {
            if (isSafe(b, row, col, num)) {
                b[row][col] = num;
                if (fillBoard(b, nextRow, nextCol)) return true;
                b[row][col] = EMPTY;
            }
        }
        return false;
    }

    private boolean isSafe(int[][] b, int row, int col, int num) {
        // linha
        for (int c = 0; c < SIZE; c++)
            if (b[row][c] == num) return false;
        // coluna
        for (int r = 0; r < SIZE; r++)
            if (b[r][col] == num) return false;
        // subgrade 3x3
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int r = boxRow; r < boxRow + 3; r++)
            for (int c = boxCol; c < boxCol + 3; c++)
                if (b[r][c] == num) return false;
        return true;
    }

    private void removeCells(int[][] b, int count) {
        // randomly remove 'count' cells to make a puzzle (not guaranteeing uniqueness)
        int removed = 0;
        while (removed < count) {
            int r = rand.nextInt(SIZE);
            int c = rand.nextInt(SIZE);
            if (b[r][c] != EMPTY) {
                b[r][c] = EMPTY;
                removed++;
            }
        }
    }

    private void printBoard(int[][] b) {
        for (int r = 0; r < SIZE; r++) {
            if (r % 3 == 0) System.out.println("+-------+-------+-------+");
            for (int c = 0; c < SIZE; c++) {
                if (c % 3 == 0) System.out.print("| ");
                int v = b[r][c];
                System.out.print((v == EMPTY ? "." : Integer.toString(v)) + " ");
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+-------+");
    }

    private int[][] copyBoard(int[][] src) {
        int[][] dst = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            System.arraycopy(src[r], 0, dst[r], 0, SIZE);
        return dst;
    }
}