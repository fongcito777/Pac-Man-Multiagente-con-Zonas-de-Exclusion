public class Pacman implements Runnable {
    private int x, y;
    private int dx, dy;
    private GameBoard board;
    private boolean running;
    public enum pacmanState { INICIAR, CAMINAR, COLISIONAR, MORIR }
    private pacmanState status;

    public Pacman(int x, int y, GameBoard board) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.dx = 0;
        this.dy = 0;
        this.running = true;
        this.status = pacmanState.INICIAR;
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
            } else { setStatus(pacmanState.COLISIONAR); }
            board.update();

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;  // Exit the loop if interrupted
            }

            // Check game state again after sleep
            if (!board.isGameRunning()) {
                setStatus(pacmanState.MORIR);
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
    public String getStatus() { return status.name(); }
    public void setStatus(pacmanState state) { this.status = state; }
}
