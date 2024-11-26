public class Pacman implements Runnable {
    private int x, y;
    private int dx, dy;
    private GameBoard board;
    private boolean running;

    public Pacman(int x, int y, GameBoard board) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.dx = 0;
        this.dy = 0;
        this.running = true;
    }

    public void stopRunning() {
        running = false;
    }

    public void run() {
        while (running && board.isGameRunning()) {  // Check both running flags
            if (!board.isWall(x + dx, y + dy)) {
                x += dx;
                y += dy;
                board.eatDot(x, y);
                board.checkCollision();
            }
            board.update();

            try {
                Thread.sleep(150);
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

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
