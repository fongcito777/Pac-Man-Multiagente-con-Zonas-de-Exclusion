import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setTitle("Pacman");
        f.setSize(600, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);

        ConfigWindow cW = new ConfigWindow(new ConfigCallback() {
            @Override
            public void onConfigComplete(String text) {
                int ghostCount = Integer.parseInt(text);
                GameBoard gameBoard = new GameBoard(ghostCount);
                f.add(gameBoard);
                f.setVisible(true);
            }
        });
    }
}
