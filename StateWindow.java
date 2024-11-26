import javax.swing.*;
import java.awt.*;

public class StateWindow {
    private JFrame frame;
    private JLabel pacmanStateLabel;
    private JLabel ghostStateLabel;

    public StateWindow() {
        frame = new JFrame("Agent States");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(2, 1));

        pacmanStateLabel = new JLabel("Pacman: ");
        //ghostStateLabel = new JLabel("Ghost: ");

        frame.add(pacmanStateLabel);
        //frame.add(ghostStateLabel);

        frame.setVisible(true);
    }

    public void updatePacmanState(String state) {
        pacmanStateLabel.setText("Pacman: " + state);
    }
}