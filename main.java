import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Новая игра");
            System.out.println("2. Результаты");
            System.out.println("3. Выход");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    game.play();
                    break;
                case 2:
                    game.showResults();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Некорректный выбор. Повторите попытку.");
                    break;
            }
        }
    }
}

class Game {
    private static final int BOARD_SIZE = 8;
    private static final int MAX_GUESSES = 15;
    private static final Map<String, int[]> SHIP_POSITIONS = new HashMap<>();

    static {
        SHIP_POSITIONS.put("A6", new int[]{1});
        SHIP_POSITIONS.put("E5", new int[]{2});
        SHIP_POSITIONS.put("F3", new int[]{3});
    }

    private char[][] board;
    private int guesses;
    private List<Integer> results;

    public Game() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        results = new ArrayList<>();
        initializeBoard();
    }

    public void play() {
        initializeBoard();
        long startTime = System.currentTimeMillis();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayBoard();
            System.out.print("Куда стреляем: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.length() != 2 || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(1))) {
                System.out.println("Некорректный формат ввода. Введите ячейку в формате A1, B2, и т.д.");
                continue;
            }

            int row = input.charAt(1) - '1';
            int col = input.charAt(0) - 'A';

            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                System.out.println("Некорректные координаты ячейки. Повторите попытку.");
                continue;
            }

            processGuess(row, col);

            if (guesses >= MAX_GUESSES) {
                System.out.println("Слишком долго! Вы проиграли.");
                return;
            }

            if (isGameOver()) {
                long endTime = System.currentTimeMillis();
                int elapsedTime = (int) ((endTime - startTime) / 1000);
                System.out.println("Поздравляем! Вы победили. Время: " + elapsedTime + " сек.");
                results.add(elapsedTime);
                return;
            }
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(board[i], ' ');
        }
    }

    private void displayBoard() {
        System.out.println("  A B C D E F G H");
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < BOARD_SIZE; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }

    private void processGuess(int row, int col) {
        guesses++;
        String cell = String.valueOf((char) ('A' + col)) + (row + 1);

        if (SHIP_POSITIONS.containsKey(cell)) {
            int[] shipId = SHIP_POSITIONS.get(cell);
            SHIP_POSITIONS.remove(cell);
            board[row][col] = 'X';

            if (isShipDestroyed(shipId)) {
                updateAdjacentCells(row, col, 'o');
                System.out.println("Корабль уничтожен!");
            } else {
                System.out.println("Попадание!");
            }
        } else {
            board[row][col] = 'o';
            System.out.println("Мимо!");
        }
    }

    private boolean isShipDestroyed(int[] shipId) {
        for (int[] pos : SHIP_POSITIONS.values()) {
            if (Arrays.equals(pos, shipId)) {
                return false;
            }
        }
        return true;
    }

    private void updateAdjacentCells(int row, int col, char value) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE && board[i][j] == ' ') {
                    board[i][j] = value;
                }
            }
        }
    }

    private boolean isGameOver() {
        return SHIP_POSITIONS.isEmpty();
    }

    public void showResults() {
        if (results.isEmpty()) {
            System.out.println("Нет доступных результатов.");
        } else {
            Collections.sort(results);
            System.out.println("Топ 3 самых быстрых игр:");
            for (int i = 0; i < Math.min(3, results.size()); i++) {
                System.out.println((i + 1) + ". " + results.get(i) + " сек.");
            }
        }
    }
}