import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame f = new JFrame();
        f.setTitle("Pacman");
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);

        CreditsWindow creditsWindow = new CreditsWindow();

        ConfigWindow cW = new ConfigWindow(new ConfigCallback() {
            @Override
            public void onConfigComplete(String text, String text2, String text3, String text4, String text5) {
                int ghostCount = Integer.parseInt(text);
                int fruitAppear = Integer.parseInt(text2);
                int fruitDisappear = Integer.parseInt(text3);
                int gridSize = Integer.parseInt(text4);
                int ghostsInZone = Integer.parseInt(text5);
                GameBoard gameBoard = new GameBoard(ghostCount, fruitAppear, fruitDisappear, gridSize, ghostsInZone);
                f.add(gameBoard);
                f.setVisible(true);

            }
        });
    }
}
