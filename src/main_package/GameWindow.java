package main_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {

    private static final int WINDOW_WIDTH = 507;
    private static final int WINDOW_HEIGHT = 555;
    private static final int WINDOW_POS_X = 400;
    private static final int WINDOW_POS_Y = 150;
    private JButton btnNewGame;
    private ConfigAndStartNewGame configAndStartNewGame;
    private Map map;

    public JButton getBtnNewGame() {

        return btnNewGame;
    }

    GameWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_POS_X, WINDOW_POS_Y);
        setTitle("TicTacToe");
        setResizable(false);
        JPanel buttonPanel = new JPanel(new GridLayout());
        btnNewGame = new JButton("New game");
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                configAndStartNewGame.setVisible(true);
            }
        });
        JButton btnExitGame = new JButton("Exit game");
        btnExitGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });
        JButton btnRestart = new JButton("Restart");
        btnRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNewGame.setEnabled(true);
                configAndStartNewGame.setVisible(true);
            }
        });
        buttonPanel.add(btnNewGame);
        buttonPanel.add(btnExitGame);
        buttonPanel.add(btnRestart);
        add(buttonPanel, BorderLayout.SOUTH);

        map = new Map();
        configAndStartNewGame = new ConfigAndStartNewGame(this);
        add(map, BorderLayout.CENTER);


        setVisible(true);
    }

    void startNewGameWithInputParam(int mode, int fieldSizeX, int fieldSizeY, int winLen) {
        map.startNewGame(mode, fieldSizeX, fieldSizeY, winLen);
    }
}
