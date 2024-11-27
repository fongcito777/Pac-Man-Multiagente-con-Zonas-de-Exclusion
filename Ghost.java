import java.awt.*;
import java.util.Random;

public class Ghost implements Runnable {
    private int x, y;
    private GameBoard board;
    private Color color;
    private Random random;
    private boolean running;
    public enum ghostState { INICIAR, PERSEGUIR, COLISIONAR, ESPERAR, MERODEAR_ZONA}
    private ghostState status;
    private boolean inBufferZone;

    public Ghost(int x, int y, GameBoard board) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.random = new Random();
        this.running = true;
        this.status = ghostState.INICIAR;
        this.inBufferZone = false;

        // Assign random color to ghost
        Color[] colors = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};
        this.color = colors[random.nextInt(colors.length)];
    }

    public void run() {
        while (running && board.isGameRunning()) {
            int dx = 0, dy = 0;
            int direction = random.nextInt(4);

            switch (direction) {
                case 0: dx = -1; break; // left
                case 1: dx = 1; break;  // right
                case 2: dy = -1; break; // up
                case 3: dy = 1; break;  // down
            }

            // Check if the move is allowed, including buffer zone restrictions
            if (!board.isWall(x + dx, y + dy)) {
                // Check buffer zone access
                int newX = x + dx;
                int newY = y + dy;

                // Check if attempting to enter buffer zone
                boolean canMove = true;
                if (isInBufferZone(newX,newY)) {
                    canMove = board.canEnterBufferZone(this);

                    if (canMove) {
                        status = ghostState.MERODEAR_ZONA;
                        inBufferZone = true;
                    } else {
                        if (!isInBufferZone(x,y)){
                            status = ghostState.ESPERAR;
                            canMove = false;
                        }
                    }
                } else {
                    // If leaving buffer zone, reset flag
                    inBufferZone = false;
                }

                // Perform move if allowed
                if (canMove) {
                    x = newX;
                    y = newY;
                    board.checkCollision();

                    // Update status if not in buffer zone
                    if (!inBufferZone) {
                        status = ghostState.PERSEGUIR;
                    }
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (!board.isGameRunning()) {
                break;
            }
        }
    }

    public boolean isInBufferZone(int x, int y) {
        return (x >= board.bufferZoneXStart &&
                x <= board.bufferZoneXEnd &&
                y >= board.bufferZoneYStart &&
                y <= board.bufferZoneYEnd);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public String getStatus() { return status.name(); }
    public void setStatus(ghostState state) { this.status = state; }
    public void stopRunning() { running = false; }
}
