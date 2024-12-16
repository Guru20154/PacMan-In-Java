import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private String[][] tileMaps = {{
        "XXXXXXXXXXXXXXXXXXX",
        "X     X   X     b X",
        "X XXX X X X XXX XXX",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X  o      P      oX",
        "XXXX X XXXXX X XXXX",
        "X    X       X    X",
        "X XX XXX X XXX XX X",
        "X  X     p     X  X",
        "XXXX XXX X XXX XXXX",
        "X       X X       X",
        "XXX XX XXXXX XX XXX",
        "X        X        X",
        "X XXX XXX X XXX XXX",
        "X   X           X  X",
        "X XX X XXXXX X XX X",
        "X  r    X   o   X X",
        "XXXXXXX X X XXXXXXX",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    },{
        "XXXXXXXXXXXXXXXXXXX",
        "X     X   X     b X",
        "X XXX X X X XXX XXX",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X  o      P      oX",
        "XXXX X XXXXX X XXXX",
        "X    X       X    X",
        "X XX XXX X XXX XX X",
        "X  X     p     X  X",
        "XXXX XXX X XXX XXXX",
        "X       X X       X",
        "XXX XX XXXXX XX XXX",
        "X        X        X",
        "X XXX XXX X XXX XXX",
        "X   X           X  X",
        "X XX X XXXXX X XX X",
        "X  r    X   o   X X",
        "XXXXXXX X X XXXXXXX",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    },
    {
        "XXXXXXXXXXXXXXXXXXX",
        "X     X   X       X",
        "X XXX X X XXX XXX X",
        "X                 X",
        "X XX XXX   XXX XX X",
        "X    r   b     P  X",
        "XXXX XXXXX XXXXXXXX",
        "X    X   p   X    X",
        "X XXX X XXX X XXX X",
        "X X     o     X   X",
        "XXX XXX X XXX XXX X",
        "X                 X",
        "X XX XXXXXXXX XX XXX",
        "X  o       X      X",
        "XXX XXXXX X XXX XXX",
        "X         X     p X",
        "X XX X XXXXX X XX X",
        "X   X   X r   X   X",
        "XXXXXXXXX X XXXXXXX",
        "X b               X",
        "XXXXXXXXXXXXXXXXXXX"
    },{
        "XXXXXXXXXXXXXXXXXXX",
        "X  X         X    X",
        "X X X XXXXX X XXX X",
        "X X X   r   X     X",
        "X XXXXXX XXXXXX XX X",
        "X      P       X   X",
        "XXX XX XXXXX XXXXXX",
        "X    X b X     X  X",
        "X XXX XXXXXXX XXX X",
        "X       p      o  X",
        "XXXXXX X XXXXX XXXX",
        "X   X   X   X     X",
        "X XXXXXX XXXXXXX XXX",
        "X X   o   X       X",
        "X XXX XXX XXXXX XXX",
        "X     X     p    X ",
        "X XX X XXXXXXX XX X",
        "X  b X     X      X",
        "XXXXXXXXX X XXXXXXX",
        "X  r               X",
        "XXXXXXXXXXXXXXXXXXX"
    },{
        "XXXXXXXXXXXXXXXXXXX",
        "Xb                X",
        "X XX XXXXXXXX XXX X",
        "X X     o        pX",
        "X XXXXXX XXXXXXXX X",
        "X     X    P     X ",
        "XXX XXX XXXXX XXXXX",
        "X       X r       X",
        "X XXXXXX XXXXXXX XX",
        "X     o X      X  X",
        "XXXXX XXXXXX XX XXXX",
        "X      X    X X    X",
        "XX XXXXXXXX XXXXX XX",
        "X       b   X    X X",
        "X XXXXXXXXXXXXXX XXX",
        "X    P    r   X    X",
        "X XXX XXXXXXX XXXXX X",
        "X    p X   X o X    X",
        "XXXXXXXXX XXXXXXXXXXX",
        "X                b  X",
        "XXXXXXXXXXXXXXXXXXX"
    }};
    
    
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; 
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    private String[] tileMap = tileMaps[random.nextInt(4)-1];

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this); 
        gameLoop.start();

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') { //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
            if(ghost.x<= 0){
                ghost.x = boardWidth-32;
            }
            else if(ghost.x+ghost.width>=boardWidth){
                ghost.x = 0;
            }
            List<Character> posdirections = new ArrayList<>();

            int ghostTileX = ghost.x / tileSize;
            int ghostTileY = ghost.y / tileSize;

            if (ghostTileX >= 0 && ghostTileX + 1 < tileMap.length &&
                ghostTileY >= 0 && ghostTileY < tileMap[ghostTileX + 1].length() &&
                tileMap[ghostTileX + 1].charAt(ghostTileY) == 'X' && ghost.direction != 'D') {
                posdirections.add('U'); // Up
            }

            if (ghostTileX - 1 >= 0 &&
                ghostTileY >= 0 && ghostTileY < tileMap[ghostTileX - 1].length() &&
                tileMap[ghostTileX - 1].charAt(ghostTileY) == 'X' && ghost.direction != 'U') {
                posdirections.add('D'); 
            }

            if (ghostTileY >= 0 && ghostTileY + 1 < tileMap[ghostTileX].length() &&
                tileMap[ghostTileX].charAt(ghostTileY + 1) == 'X' && ghost.direction != 'L') {
                posdirections.add('R'); 
            }

            if (ghostTileY - 1 >= 0 && ghostTileY < tileMap[ghostTileX].length() &&
                tileMap[ghostTileX].charAt(ghostTileY - 1) == 'X' && ghost.direction != 'R') {
                posdirections.add('L'); 
            }

            if (!posdirections.isEmpty()) {
                char newDirection = posdirections.get(random.nextInt(posdirections.size()));
                ghost.updateDirection(newDirection);
            }

            posdirections.clear();
            
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            tileMap = tileMaps[random.nextInt(4)-1];
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}
