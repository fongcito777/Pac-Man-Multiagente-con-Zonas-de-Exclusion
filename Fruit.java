import java.util.Random;

public class Fruit implements Runnable{
    private int x, y;
    private boolean visible;
    private boolean running;
    private GameBoard board;
    private Random random;
    private static final int BOARD_WIDTH = 28;
    private static final int BOARD_HEIGHT = 28;
    private static final int APPEAR_MIN_TIME = 5000; // 5 seconds
    private static final int APPEAR_MAX_TIME = 10000; // 10 seconds
    private static int APPEAR_TIME = 2000;
    private static int VISIBLE_TIME = 6000;
    public enum fruitState { INICIAR, APARECER, COLISIONAR, DESAPARECER }
    private fruitState status;

    public Fruit(GameBoard board, int appearTime, int disappearTime) {
        this.board = board;
        this.random = new Random();
        this.visible = false;
        this.running = true;
        APPEAR_TIME = appearTime*1000;
        VISIBLE_TIME = disappearTime*1000;
        setStatus(fruitState.INICIAR);
    }

    @Override
    public void run() {
        while (running && board.isGameRunning()) {
            try {
                // Wait random time before appearing
                Thread.sleep(APPEAR_TIME);
                //Thread.sleep(random.nextInt(APPEAR_MAX_TIME - APPEAR_MIN_TIME) + APPEAR_MIN_TIME);

                // Find random valid position
                do {
                    x = random.nextInt(BOARD_WIDTH - 2) + 1;
                    y = random.nextInt(BOARD_HEIGHT - 2) + 1;
                } while (board.isWall(x, y));

                visible = true;
                setStatus(fruitState.APARECER);
                board.update();

                // Stay visible for some time
                //Thread.sleep(VISIBLE_TIME);
                synchronized (this){ wait(VISIBLE_TIME); }

                setStatus(fruitState.DESAPARECER);
                visible = false;
                board.update();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public synchronized void collect() {
        visible = false;
        setStatus(fruitState.COLISIONAR);
        notifyAll();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVisible() { return visible; }
    public void stopRunning() {
        running = false;
    }
    public String getStatus() { return status.name(); }
    public void setStatus(fruitState status) { this.status = status; }
}
