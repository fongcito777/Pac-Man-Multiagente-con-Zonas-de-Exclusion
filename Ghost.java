import java.awt.*;
import java.util.Random;

public class Ghost implements Runnable {
    private int x, y;
    private GameBoard board;
    private Color color;
    private Random random;
    private boolean running;  // Add running flag

    public Ghost(int x, int y, GameBoard board) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.random = new Random();
        this.running = true;  // Initialize running as true

        // Assign random color to ghost
        Color[] colors = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};
        this.color = colors[random.nextInt(colors.length)];
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        while (running && board.isGameRunning()) {  // Check both running flags
            // Simple random movement
            int dx = 0, dy = 0;
            int direction = random.nextInt(4);

            switch (direction) {
                case 0: dx = -1; break; // left
                case 1: dx = 1; break;  // right
                case 2: dy = -1; break; // up
                case 3: dy = 1; break;  // down
            }

            if (!board.isWall(x + dx, y + dy)) {
                x += dx;
                y += dy;
                board.checkCollision();
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;  // Exit the loop if interrupted
            }

            // Check game state again after sleep
            if (!board.isGameRunning()) {
                break;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
}
