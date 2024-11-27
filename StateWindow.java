import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StateWindow {
    private JFrame frame;
    private JLabel pacmanStateLabel;
    //private JLabel ghostStateLabel;
    private ArrayList<JLabel> ghostStateLabels;
    private JLabel fruitStateLabel;
    private int ghostAmount;

    public StateWindow() {
        frame = new JFrame("Agent States");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(2, 1));

        pacmanStateLabel = new JLabel("Pacman: ");
        frame.add(pacmanStateLabel);

        fruitStateLabel = new JLabel("Fruit: ");
        frame.add(fruitStateLabel);

        frame.setVisible(true);
    }

    public void addGhostsLabels(int ghostNum) {
        frame.setLayout(new GridLayout(ghostNum+2, 1));
        ghostAmount = ghostNum;
        ghostStateLabels = new ArrayList<>();
        for (int i = 0; i < ghostAmount; i++) {
            JLabel ghost = new JLabel("Ghost " + (i + 1) + ": ");
            ghostStateLabels.add(ghost);
            frame.add(ghost);
        }
    }

    public void updatePacmanState(String state) {
        pacmanStateLabel.setText("Pacman: " + state);
    }
    public void updateGhostState(String state, int ghost) {
        if (ghostStateLabels!=null) { ghostStateLabels.get(ghost).setText("Ghost " + (ghost + 1) + ": " + state); }
    }
    public void updateFruitState(String state) {
        fruitStateLabel.setText("Fruit: " + state);
    }
}
