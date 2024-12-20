import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class GameBoard extends JPanel implements KeyListener {
    private static final int CELL_SIZE = 20;
    private static final int BOARD_WIDTH = 28;
    private static final int BOARD_HEIGHT = 28;

    // BufferZone configuration
    private static int BUFFER_ZONE_SIZE = 10; // Grid size of bufferzone
    private static int MAX_GHOSTS_IN_BUFFER_ZONE = 2; // Max amount of ghosts in bufferzone

    public int bufferZoneXStart;
    public int bufferZoneXEnd;
    public int bufferZoneYStart;
    public int bufferZoneYEnd;

    private Pacman pacman;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Thread> ghostThreads;
    private Fruit fruit;
    private ArrayList<Ghost> ghostsInBufferZone;
    private int[][] board;
    private int score;
    private int totalDots;
    private boolean gameRunning;
    private long gameStartTime;
    private static final long SAFE_PERIOD = 3000; // 3 seconds safe period

    private int ghostNumber, fruitAppearTime, fruitDisappearTime;

    public boolean running;
    public StateWindow stateWindow = new StateWindow();
    public ThreadWindow threadWindow = new ThreadWindow();

    public Thread pacmanThread;
    public Thread fruitThread;

    private Clip clip;

    public GameBoard(int gN, int appear, int disappear, int gridSize, int ghostZone) {
        setFocusable(true);
        addKeyListener(this);
        ghostNumber = gN;
        fruitAppearTime = appear;
        fruitDisappearTime = disappear;
        BUFFER_ZONE_SIZE = gridSize;
        MAX_GHOSTS_IN_BUFFER_ZONE = ghostZone;
        initGame();
        initRandomBufferZone();
        stateWindow.addGhostsLabels(gN);
        threadWindow.addGhostsLabels(gN);
        ghostsInBufferZone = new ArrayList<>();
        playSound("clip.wav");
    }

    private void initGame() {
        running = true;
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        totalDots = 0;
        if (ghostsInBufferZone != null) { ghostsInBufferZone.clear(); }

        // Create symmetrical walls
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                // Always create outer border
                if (i == 0 || i == BOARD_HEIGHT-1 || j == 0 || j == BOARD_WIDTH-1) {
                    board[i][j] = 1;
                }
                // Exclude center box from dots
                else if (i >= 12 && i <= 16 && j >= 12 && j <= 16);  // Do nothing
                else {
                    board[i][j] = 2; // Default to dot
                    totalDots++;
                }
            }
        }

        // Symmetric wall patterns
        int[][] symmetricWalls = {
                // Vertical center walls
                {14, 2, 14, 8},
                {13, 2, 13, 8},

                // Horizontal center walls
                {2, 14, 8, 14},
                {2, 13, 8, 13},

                // Diagonal walls (symmetric)
                {5, 5, 9, 9},
                {22, 5, 18, 9},

                // Center box
                {12, 17, 16, 17},
                {12, 12, 12, 17},
                {16, 12, 16, 17},

                // Additional symmetric structures
                {3, 20, 3, 25},
                {24, 20, 24, 25},
                {7, 22, 11, 22},
                {16, 22, 20, 22}
        };

        // Add symmetric walls
        for (int[] wall : symmetricWalls) {
            drawSymmetricLine(wall[0], wall[1], wall[2], wall[3]);
        }

        score = 0;
        gameRunning = true;
        gameStartTime = System.currentTimeMillis();

        // Initialize Pacman at bottom center
        pacman = new Pacman(14, 20, this);
        pacmanThread = new Thread(pacman);
        pacmanThread.start();

        // Initialize Ghosts in their starting positions
        ghosts = new ArrayList<>();
        ghostThreads = new ArrayList<>();

        int[][] ghostSpawns = {
                {13, 11}, // Top left of center
                {15, 11}, // Top right of center
                {13, 13}, // Bottom left of center
                {15, 13}  // Bottom right of center
        };

        for (int i = 0; i < ghostNumber; i++) {
            Ghost ghost = new Ghost(ghostSpawns[i%4][0], ghostSpawns[i%4][1], this);
            ghosts.add(ghost);
            Thread ghostThread = new Thread(ghost);
            ghostThreads.add(ghostThread);
            ghostThread.start();
        }

        //Initialize Fruit
        fruit = new Fruit(this, fruitAppearTime, fruitDisappearTime);
        fruitThread = new Thread(fruit);
        fruitThread.start();
    }

    private void initRandomBufferZone() {
        Random random = new Random();

        // Decide which corner (0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right)
        int corner = random.nextInt(4);

        switch (corner) {
            case 0: // Top-left corner
                bufferZoneXStart = 1;
                bufferZoneYStart = 1;
                break;
            case 1: // Top-right corner
                bufferZoneXStart = BOARD_WIDTH - BUFFER_ZONE_SIZE - 1;
                bufferZoneYStart = 1;
                break;
            case 2: // Bottom-left corner
                bufferZoneXStart = 1;
                bufferZoneYStart = BOARD_HEIGHT - BUFFER_ZONE_SIZE - 1;
                break;
            case 3: // Bottom-right corner
                bufferZoneXStart = BOARD_WIDTH - BUFFER_ZONE_SIZE - 1;
                bufferZoneYStart = BOARD_HEIGHT - BUFFER_ZONE_SIZE - 1;
                break;
        }

        // Calculate end coordinates
        bufferZoneXEnd = bufferZoneXStart + BUFFER_ZONE_SIZE - 1;
        bufferZoneYEnd = bufferZoneYStart + BUFFER_ZONE_SIZE - 1;

        // Initialize buffer zone list
        ghostsInBufferZone = new ArrayList<>();
    }

    private void drawSymmetricLine(int x1, int y1, int x2, int y2) {
        drawLine(x1, y1, x2, y2);

        int centerX = BOARD_WIDTH / 2;
        drawLine(centerX - (x1 - centerX), y1, centerX - (x2 - centerX), y2);
    }

    // Helper method to draw a line of walls
    private void drawLine(int x1, int y1, int x2, int y2) {

        if (y1 == y2) {
            int start = Math.min(x1, x2);
            int end = Math.max(x1, x2);
            for (int x = start; x <= end; x++) {
                board[y1][x] = 1;
                totalDots--;
            }
        }

        else if (x1 == x2) {
            int start = Math.min(y1, y2);
            int end = Math.max(y1, y2);
            for (int y = start; y <= end; y++) {
                board[y][x1] = 1;
                totalDots--;
            }
        }

        else {
            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);
            int sx = x1 < x2 ? 1 : -1;
            int sy = y1 < y2 ? 1 : -1;
            int err = dx - dy;

            while (true) {
                board[y1][x1] = 1;
                totalDots--;

                if (x1 == x2 && y1 == y2) break;

                int e2 = 2 * err;
                if (e2 > -dy) {
                    err -= dy;
                    x1 += sx;
                }
                if (e2 < dx) {
                    err += dx;
                    y1 += sy;
                }
            }
        }
    }

    // Modify the eatDot method to track dot collection
    public void eatDot(int x, int y) {
        if (board[y][x] == 2) {
            board[y][x] = 0;
            score += 10;
            totalDots--;  // Decrement total dots when a dot is eaten

            // Check if all dots have been collected
            if (totalDots <= 0) {
                stateWindow.updatePacmanState("VICTORY");
                stopGame();
                JOptionPane.showMessageDialog(this, "Congratulations! You won! Score: " + score);
                initGame();  // Restart the game
            }
        }
    }
    // Add method to stop all game entities
    private void stopGame() {
        gameRunning = false;
        // Stop Pacman
        if (pacman != null) {
            pacman.stopRunning();
        }
        // Stop all ghosts
        if (ghosts != null) {
            for (Ghost ghost : ghosts) {
                ghost.stopRunning();
            }
        }
        // Stop Fruit
        if (fruit != null) {
            fruit.stopRunning();
        }
    }

    public boolean canEnterBufferZone(Ghost ghost) {
        // Check if ghost is in buffer zone coordinates
        boolean inBufferZoneArea = (ghost.getX() >= bufferZoneXStart &&
                ghost.getX() <= bufferZoneXEnd &&
                ghost.getY() >= bufferZoneYStart &&
                ghost.getY() <= bufferZoneYEnd);

        // If in buffer zone area, check occupancy
        if (inBufferZoneArea) {
            if (ghostsInBufferZone.size() < MAX_GHOSTS_IN_BUFFER_ZONE) {
                // If not already in buffer zone list, add
                if (!ghostsInBufferZone.contains(ghost)) {
                    ghostsInBufferZone.add(ghost);
                }
                return true;
            } else {
                // Buffer zone is full
                return false;
            }
        }

        // Remove ghost from buffer zone if it leaves the area
        if (ghostsInBufferZone!=null) { ghostsInBufferZone.remove(ghost); }
        return true;
    }

    // Add method to check if game is running
    public boolean isGameRunning() {
        return gameRunning;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == 1) {
                    g.setColor(Color.BLUE);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (board[i][j] == 2) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * CELL_SIZE + 8, i * CELL_SIZE + 8, 4, 4);
                }
            }
        }

        // Draw Pacman
        g.setColor(Color.YELLOW);
        g.fillArc(pacman.getX() * CELL_SIZE, pacman.getY() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE, 30, 300);

        // Draw Ghosts
        for (Ghost ghost : ghosts) {
            g.setColor(ghost.getColor());
            g.fillRect(ghost.getX() * CELL_SIZE, ghost.getY() * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);
        }

        // Draw Fruit
        if (fruit.isVisible()) {
            stateWindow.updateFruitState(fruit.getStatus());
            g.setColor(Color.RED);
            g.fillOval(fruit.getX() * CELL_SIZE, fruit.getY() * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);
        }

        // Draw Score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, BOARD_HEIGHT * CELL_SIZE + 20);

        // Draw safety period indicator
        long elapsedTime = System.currentTimeMillis() - gameStartTime;
        if (elapsedTime < SAFE_PERIOD) {
            g.setColor(Color.GREEN);
            g.drawString("SAFE TIME: " + ((SAFE_PERIOD - elapsedTime) / 1000 + 1) + "s",
                    200, BOARD_HEIGHT * CELL_SIZE + 20);
        }
        // Highlight buffer zone
        g.setColor(new Color(100, 100, 100, 50)); // Semi-transparent gray
        g.fillRect(bufferZoneXStart * CELL_SIZE,
                bufferZoneYStart * CELL_SIZE,
                (bufferZoneXEnd - bufferZoneXStart + 1) * CELL_SIZE,
                (bufferZoneYEnd - bufferZoneYStart + 1) * CELL_SIZE);
    }

    public void keyPressed(KeyEvent e) {
        if (!gameRunning) return;
        pacman.setStatus(Pacman.pacmanState.CAMINAR);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                pacman.setDirection(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                pacman.setDirection(1, 0);
                break;
            case KeyEvent.VK_UP:
                pacman.setDirection(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                pacman.setDirection(0, 1);
                break;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public boolean isWall(int x, int y) {
        return board[y][x] == 1;
    }

    public void checkCollision() {
        // Only check collisions after safe period
        if (System.currentTimeMillis() - gameStartTime < SAFE_PERIOD) {
            return;
        }

        if (!Objects.equals(pacman.getStatus(), Pacman.pacmanState.INICIAR.name())) {
            pacman.setStatus(Pacman.pacmanState.CAMINAR);
        }

        for (Ghost ghost : ghosts) {
            if (pacman.getX() == ghost.getX() && pacman.getY() == ghost.getY()) {
                stateWindow.updatePacmanState("TERMINATED");
                pacman.setStatus(Pacman.pacmanState.COLISIONAR);
                ghost.setStatus(Ghost.ghostState.COLISIONAR);
                stopGame(); // Call stopGame instead of just setting gameRunning to false
                JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
                initGame();
                break;
            }
        }

        // Check fruit collision
        if (fruit.isVisible() && pacman.getX() == fruit.getX() && pacman.getY() == fruit.getY()) {
            fruit.collect();
            pacman.setStatus(Pacman.pacmanState.COLISIONAR);
            stateWindow.updateFruitState(fruit.getStatus());
            score += 100; // Bonus points for fruit
        }
    }

    public void update() {
        //stateWindow.updatePacmanState(pacmanThread.getState().toString());
        try{ Thread.sleep(1); } catch (InterruptedException e) { System.out.println(e); }
        if (pacman!=null) { stateWindow.updatePacmanState(pacman.getStatus()); }
        if (fruit!=null) { stateWindow.updateFruitState(fruit.getStatus()); }
        if (pacmanThread!=null) { threadWindow.updatePacmanState(pacmanThread.getState().toString()); }
        if (fruitThread!=null) { threadWindow.updateFruitState(fruitThread.getState().toString()); }
        for (Ghost ghost : ghosts) {
            if (ghost!=null){ stateWindow.updateGhostState(ghost.getStatus(),ghosts.indexOf(ghost)); }
        }
        for (Thread ghostThread : ghostThreads) {
            if (ghostThread!=null) { threadWindow.updateGhostState(ghostThread.getState().toString(), ghostThreads.indexOf(ghostThread)); }
        }
        repaint();
    }

    public void playSound(String filePath) {
        try {
            // Open an audio input stream.
            File soundFile = new File(filePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource.
            clip = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);

            // Loop the clip continuously.
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // Start the clip.
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
