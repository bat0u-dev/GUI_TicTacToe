package main_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;


public class Map extends JPanel {

    public int gameMode;
    public static final int GAME_MODE_HVA = 0;
    public static final int GAME_MODE_HVH = 1;
    private static final int DOT_EMPTY = 0;
    private static final int DOT_HUM = 1;
    private static final int DOT_AI = 2;
    private static final int DOT_HUM_PL1 = 1;
    private static final int DOT_HUM_PL2 = 2;
    private static final int PADDING = 5;
    private static final Random rnd = new Random();

    private static final int STATE_DRAW = 0;
    private static final int STATE_HUM_WIN = 1;
    private static final int STATE_AI_WIN = 2;
    private static final int STATE_PL1_WIN = 3;
    private static final int STATE_PL2_WIN = 4;
    private int turnCounter = 0;
    private int stateGAmeOver;

    private int[][] field;
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLen;
    private int cellWidth;
    private int cellHeight;
    private boolean initialized;
    private boolean isGameOver;

    private String MSG_DRAW = "Ничья!";
    private String MSG_AI_WIN = "Победил компьютер!";
    private String MSG_HUM_WIN = "Вы победили!";
    private String MSG_HUM_PL1_WIN = "Победил игрок 1!";
    private String MSG_HUM_PL2_WIN = "Победил игрок 2!";

    Map() {
        setBackground(Color.ORANGE);
    }

    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLen) {
        this.gameMode = mode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLen = winLen;
        field = new int[fieldSizeX][fieldSizeY];
        System.out.println(field);// компонент отладки. После отладки УДАЛИТЬ!!!
        debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
        initialized = true;
        isGameOver = false;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println(field);// компонент отладки. После отладки УДАЛИТЬ!!!
                debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
                update(e);
                repaint();
            }
        });
        repaint();
    }

    private void update(MouseEvent e) {
        if (gameMode == GAME_MODE_HVA) {

            if (isGameOver) return;
            int cellX = e.getX() / cellWidth;
            int cellY = e.getY() / cellHeight;
            if (!isEmptyCell(cellX, cellY) || !isValidCell(cellX, cellY)) {
                return;
            }
            field[cellY][cellX] = DOT_HUM;
            new Thread(new Runnable() {//Поток добавлен для реализации задержки хода AI, чтобы не затрагивать
                //      поток paintComponent();
                @Override
                public void run() {
                    if (checkWin(DOT_HUM)) {
                        stateGAmeOver = STATE_HUM_WIN;
                        isGameOver = true;
                        return;
                    }
                    debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
                    if (isMapFull()) {
                        stateGAmeOver = STATE_DRAW;
                        isGameOver = true;
                        return;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    aiTurn();
                    System.out.println("Ход компа.");// компонент отладки. После отладки УДАЛИТЬ!!!
                    debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
                    repaint();
                    System.out.println("Отрисовка хода компа.");// компонент отладки. После отладки УДАЛИТЬ!!!
                    if (checkWin(DOT_AI)) {
                        stateGAmeOver = STATE_AI_WIN;
                        isGameOver = true;
                        return;
                    }
                    if (isMapFull()) {
                        stateGAmeOver = STATE_DRAW;
                        isGameOver = true;
                    }
                }
            }).start();
        }
        if (gameMode == GAME_MODE_HVH) {
            if (isGameOver) return;
//Ход игрока 1:
            if (turnCounter % 2 != 0) {

                int CellXPl1 = e.getX() / cellWidth;
                int CellYPl1 = e.getY() / cellHeight;
                if (!isEmptyCell(CellXPl1, CellYPl1) || !isValidCell(CellXPl1, CellYPl1)) return;
                field[CellYPl1][CellXPl1] = DOT_HUM_PL1;
                turnCounter++;
                System.out.println("Ход номер " + turnCounter);// компонент отладки. После отладки УДАЛИТЬ!!!
                System.out.println(field);// компонент отладки. После отладки УДАЛИТЬ!!!
                debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
                if (checkWin(DOT_HUM_PL1)) {
                    isGameOver = true;
                    stateGAmeOver = STATE_PL1_WIN;
                }
                if (isMapFull()) {
                    isGameOver = true;
                    stateGAmeOver = STATE_DRAW;
                }
            }
//Ход игрока 2:
            if (turnCounter % 2 == 0) {
                int CellXPl2 = e.getX() / cellWidth;
                int CellYPl2 = e.getY() / cellHeight;
                if (!isEmptyCell(CellXPl2, CellYPl2) || !isValidCell(CellXPl2, CellYPl2)) return;
                field[CellYPl2][CellXPl2] = DOT_HUM_PL2;
                turnCounter++;
                System.out.println("Ход номер " + turnCounter);// компонент отладки. После отладки УДАЛИТЬ!!!
                System.out.println(field);// компонент отладки. После отладки УДАЛИТЬ!!!
                debugHelper();// компонент отладки. После отладки УДАЛИТЬ!!!
                if (checkWin(DOT_HUM_PL2)) {
                    isGameOver = true;
                    stateGAmeOver = STATE_PL2_WIN;
                }
                if (isMapFull()) {
                    isGameOver = true;
                    stateGAmeOver = STATE_DRAW;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        if (!initialized) return;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        cellWidth = panelWidth / fieldSizeY;
        cellHeight = panelHeight / fieldSizeX;
        g.setColor(Color.BLACK);

        for (int i = 0; i <= panelHeight; i++) {
            int y = cellWidth * i;
            int x = cellHeight * i;
            g.drawLine(0, y, panelWidth, y);
            g.drawLine(x, 0, x, panelHeight);
        }

        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isEmptyCell(x, y)) continue;
                if (gameMode == GAME_MODE_HVA) {
                    if (field[y][x] == DOT_HUM) {
                        g.setColor(Color.BLUE);
                    } else if (field[y][x] == DOT_AI) {
                        g.setColor(Color.RED);
                    } else {
                        throw new RuntimeException("Can't recognize cell " + field[y][x]);
                    }
                    g.fillOval(x * cellWidth + PADDING, y * cellHeight + PADDING,
                            cellWidth - PADDING * 2, cellHeight - PADDING * 2);
                }

                if (gameMode == GAME_MODE_HVH) {
                    if (field[y][x] == DOT_HUM_PL1) {
                        g.setColor(Color.DARK_GRAY);
                    } else if (field[y][x] == DOT_HUM_PL2) {
                        g.setColor(Color.MAGENTA);
                    } else {
                        throw new RuntimeException("Can't recognize cell " + field[y][x]);
                    }
                    g.fillOval(x * cellWidth + PADDING, y * cellHeight + PADDING,
                            cellWidth - PADDING * 2, cellHeight - PADDING * 2);
                }
            }
        }
        if (isGameOver) {
            showFinalMessage(g);
            System.out.println("Конец игры");// компонент отладки. После отладки УДАЛИТЬ!!!
        }
    }

    private void aiTurn() {
        if (aiTurnWinCell()) return;
        if (humanTurnWinCell()) return;
        int x, y;
        do {
            y = rnd.nextInt(fieldSizeY);
            x = rnd.nextInt(fieldSizeX);
        } while (!(isEmptyCell(x, y)));
        field[y][x] = DOT_AI;
    }

    private boolean aiTurnWinCell() {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = DOT_AI;
                    if (checkWin(DOT_AI)) return true;
                    field[i][j] = DOT_EMPTY;
                }
            }
        }
        return false;
    }

    private boolean humanTurnWinCell() {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = DOT_HUM;
                    if (checkWin(DOT_HUM)) {
                        field[i][j] = DOT_AI;
                        return true;
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }
        return false;
    }

    private boolean checkWin(int dot) {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (checkLine(i, j, 1, 0, winLen, dot)) return true;
                if (checkLine(i, j, 1, 1, winLen, dot)) return true;
                if (checkLine(i, j, 0, 1, winLen, dot)) return true;
                if (checkLine(i, j, 1, -1, winLen, dot)) return true;
            }
        }
        return false;
    }

    private boolean checkLine(int x, int y, int vx, int vy, int len, int dot) {
        int far_x = x + (len - 1) * vx;
        int far_y = y + (len - 1) * vy;
        if (!isValidCell(far_x, far_y)) return false;
        for (int i = 0; i < len; i++) {
            if (field[y + i * vy][x + i * vx] != dot) return false;
        }
        return true;
    }

    private boolean isMapFull() {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[i][j] == DOT_EMPTY) return false;
            }
        }
        return true;
    }

    private boolean isValidCell(int x, int y) {

        return 0 <= x && x < fieldSizeX && 0 <= y && y < fieldSizeY;
    }

    private boolean isEmptyCell(int x, int y) {
        return field[y][x] == DOT_EMPTY;
    }

    private void showFinalMessage(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(20, 150, getWidth() - 40, 170);
        g.setColor(Color.BLACK);
        g.drawRect(20, 150, getWidth() - 40, 170);
        g.setFont(new Font("Times new roman", Font.BOLD, 48));
        switch (stateGAmeOver) {
            case STATE_DRAW:
                g.drawString(MSG_DRAW, 160, getHeight() / 2);
                break;
            case STATE_AI_WIN:
                g.setFont(new Font("Times new roman", Font.BOLD, 46));
                g.drawString(MSG_AI_WIN, 30, getHeight() / 2);
                break;
            case STATE_HUM_WIN:
                g.setFont(new Font("Times new roman", Font.BOLD, 46));
                g.drawString(MSG_HUM_WIN, 100, getHeight() / 2);
                break;
            case STATE_PL1_WIN:
                g.setFont(new Font("Times new roman", Font.BOLD, 46));
                g.drawString(MSG_HUM_PL1_WIN,65,getHeight()/2);
                break;
            case STATE_PL2_WIN:
                g.setFont(new Font("Times new roman", Font.BOLD, 46));
                g.drawString(MSG_HUM_PL2_WIN,65,getHeight()/2);
                break;
            default:
                throw new RuntimeException("Unknown gameover stat :" + stateGAmeOver);

        }
    }

    private void debugHelper() {//служебный метод для отладки. После отладки УДАЛИТЬ ВЕЗДЕ!!!
        for (int i = 0; i < field.length; i++) {
            System.out.println(Arrays.toString(field[i]));

        }
        System.out.println(field.length);
        System.out.println();

    }
}
