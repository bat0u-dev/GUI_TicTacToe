package main_package;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigAndStartNewGame extends JFrame {
    private static final int WINDOW_WIDTH = 350;
    private static final int WINDOW_HEIGHT = 230;
    private static final int MIN_WIN_LENGTH = 3;
    private static final int MIN_FIELD_SIZE = 3;
    private static final int MAX_FIELD_SIZE = 10;
    private static final String FIELD_SIZE_PREFIX = "Field size is: ";
    private static final String WIN_LENGTH_PREFIX = "Win length is: ";
    private GameWindow gameWindow;
    private JRadioButton humVSAi;
    private JRadioButton humVsHum;
    private JSlider slFieldSize;
    private JSlider slWinLen;

    ConfigAndStartNewGame(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Config and start");
        Rectangle gameWindowBounds = gameWindow.getBounds();
        int posX = (int) gameWindowBounds.getCenterX() - WINDOW_WIDTH / 2;
        int posY = (int) gameWindowBounds.getCenterY() - WINDOW_HEIGHT / 2;
        setLocation(posX, posY);
        setLayout(new GridLayout(10, 1));
        JButton btnStart = new JButton("START");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                clickBtnStart();
            }
        });

        addControlGameMode();
        addControlFieldConfig();
        add(btnStart);

    }

    private void addControlGameMode() {
        add(new JLabel("Options"));
        humVSAi = new JRadioButton("1 Player");
        humVsHum = new JRadioButton("2 Player's");
        ButtonGroup gameMode = new ButtonGroup();
        add(humVSAi);
        add(humVsHum);
        gameMode.add(humVSAi);
        gameMode.add(humVsHum);
        humVSAi.setSelected(true);
//      humVSAi.doClick();// программная имитация клика пользователя с таймером отпускания,
//      вешает загрузку компонента и программы*
    }

    private void addControlFieldConfig() {
        JLabel lblFieldSize = new JLabel(FIELD_SIZE_PREFIX + MIN_FIELD_SIZE);
        slFieldSize = new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE);
        JLabel lblWinLen = new JLabel(FIELD_SIZE_PREFIX + MIN_WIN_LENGTH);
        slFieldSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentValue = slFieldSize.getValue();
                lblFieldSize.setText(FIELD_SIZE_PREFIX + currentValue);
                slWinLen.setMaximum(currentValue);
                slWinLen.setEnabled(true);
                //как сделать, чтобы доступность поля не устанавливалась многократно при каждом изменении?
                //насколько может быть критично?
            }
        });
        slWinLen = new JSlider(MIN_WIN_LENGTH, MAX_FIELD_SIZE, MIN_WIN_LENGTH);
        slWinLen.setEnabled(false);
        slWinLen.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblWinLen.setText(WIN_LENGTH_PREFIX + slWinLen.getValue());
            }
        });


        add(new JLabel("Choose field size: "));
        add(slFieldSize);
        add(lblFieldSize);
        add(new JLabel("Choose winning length: "));
        add(slWinLen);
        add(lblWinLen);

    }

    private void clickBtnStart() {

        int gameMode;
        if (humVSAi.isSelected())
            gameMode = Map.GAME_MODE_HVA;
        else if (humVsHum.isSelected())
            gameMode = Map.GAME_MODE_HVH;
        else
            throw new RuntimeException("Unknown mode radio button selected!");

        int fieldSize = slFieldSize.getValue();
        int winLen = slWinLen.getValue();
        gameWindow.startNewGameWithInputParam(gameMode, fieldSize, fieldSize, winLen);
        gameWindow.getBtnNewGame().setEnabled(false);
        setVisible(false);
    }
}
