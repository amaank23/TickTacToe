import java.util.Scanner;

class Board {
    private char[][] grid;
    private final int SIZE = 3;

    public Board() {
        grid = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    public void displayBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j]);
                if (j < SIZE - 1) System.out.print(" | ");
            }
            System.out.println();
            if (i < SIZE - 1) System.out.println("--+---+--");
        }
    }

    public boolean updateCell(int row, int col, char mark) {
        if (grid[row][col] == ' ') {
            grid[row][col] = mark;
            return true;
        }
        return false;
    }

    public boolean checkWin(char mark) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][0] == mark && grid[i][1] == mark && grid[i][2] == mark) return true;
            if (grid[0][i] == mark && grid[1][i] == mark && grid[2][i] == mark) return true;
        }
        return (grid[0][0] == mark && grid[1][1] == mark && grid[2][2] == mark) ||
                (grid[0][2] == mark && grid[1][1] == mark && grid[2][0] == mark);
    }

    public boolean isDraw() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == ' ') return false;
            }
        }
        return true;
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE && grid[row][col] == ' ';
    }

    public void undoMove(int row, int col) {
        grid[row][col] = ' ';
    }

    public void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = ' ';
            }
        }
    }
}

abstract class Player {
    protected char mark;

    public Player(char mark) {
        this.mark = mark;
    }

    public char getMark() {
        return mark;
    }

    public abstract int[] makeMove(Board board);
}

class HumanPlayer extends Player {
    private Scanner scanner;

    public HumanPlayer(char mark, Scanner scanner) {
        super(mark);
        this.scanner = scanner;
    }

    @Override
    public int[] makeMove(Board board) {
        int row, col;
        do {
            System.out.print("Enter row number (0-2): ");
            row = scanner.nextInt();
            System.out.print("Enter column number (0-2): ");
            col = scanner.nextInt();
            if (!board.isValidMove(row, col)) {
                System.out.println("Cell already selected or invalid move. Try again.");
            }
        } while (!board.isValidMove(row, col));
        return new int[]{row, col};
    }
}

class AIPlayer extends Player {
    private char opponentMark;

    public AIPlayer(char mark, char opponentMark) {
        super(mark);
        this.opponentMark = opponentMark;
    }

    @Override
    public int[] makeMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isValidMove(i, j)) {
                    board.updateCell(i, j, mark);
                    int score = minimax(board, false);
                    board.undoMove(i, j);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(Board board, boolean isMaximizing) {
        if (board.checkWin(mark)) return 1;
        if (board.checkWin(opponentMark)) return -1;
        if (board.isDraw()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.isValidMove(i, j)) {
                        board.updateCell(i, j, mark);
                        int score = minimax(board, false);
                        board.undoMove(i, j);
                        bestScore = Math.max(bestScore, score);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.isValidMove(i, j)) {
                        board.updateCell(i, j, opponentMark);
                        int score = minimax(board, true);
                        board.undoMove(i, j);
                        bestScore = Math.min(bestScore, score);
                    }
                }
            }
            return bestScore;
        }
    }
}

class GameManager {
    private Board board;
    private Player player1, player2;
    private Scanner scanner;

    public GameManager(Player player1, Player player2, Scanner scanner) {
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.scanner = scanner;
    }

    public void startGame() {
        boolean playAgain;
        do {
            board.resetBoard();
            Player currentPlayer = player1;
            while (true) {
                board.displayBoard();
                System.out.println("Player " + currentPlayer.getMark() + "'s turn.");
                int[] move = currentPlayer.makeMove(board);
                board.updateCell(move[0], move[1], currentPlayer.getMark());

                if (board.checkWin(currentPlayer.getMark())) {
                    board.displayBoard();
                    System.out.println("Player " + currentPlayer.getMark() + " wins!");
                    break;
                }

                if (board.isDraw()) {
                    board.displayBoard();
                    System.out.println("It's a draw!");
                    break;
                }

                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }

            System.out.print("Do you want to play again? (yes/no): ");
            playAgain = scanner.next().equalsIgnoreCase("yes");
        } while (playAgain);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Player human = new HumanPlayer('X', scanner);
        Player ai = new AIPlayer('O', 'X');
        GameManager gameManager = new GameManager(human, ai, scanner);

        System.out.println("Welcome to Tic-Tac-Toe!");
        gameManager.startGame();
    }
}
