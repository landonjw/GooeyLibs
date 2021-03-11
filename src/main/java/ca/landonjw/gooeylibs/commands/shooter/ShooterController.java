package ca.landonjw.gooeylibs.commands.shooter;

import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.commands.shooter.enemies.LargeAsteroid;
import ca.landonjw.gooeylibs.commands.shooter.enemies.MediumAsteroid;
import ca.landonjw.gooeylibs.commands.shooter.enemies.SmallAsteroid;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ShooterController extends UpdateEmitter<ShooterController> {

    /**
     * The health the player has. When this hits 0, the game is over.
     */
    private int health = 10;
    /**
     * If the health requires an update in the page.
     */
    private boolean healthDirty;

    /**
     * The number of times the game loop has occurred.
     */
    private int timesLooped;

    /**
     * The player's 'ship'. This can move on the horizontal axis only.
     */
    private final PlayerShip player = new PlayerShip();
    /**
     * The list of enemy entities currently spawned in the game.
     */
    private final List<ShooterEnemy> enemies = Lists.newArrayList();
    /**
     * The list of projectile entities currently spawned in the game.
     */
    private final List<ShooterProjectile> projectiles = Lists.newArrayList();

    private boolean gameOver;
    private Task gameLoop;

    private final Random random = new Random();

    public ShooterController() {
        this.gameLoop = Task.builder()
                .execute(this::loop)
                .interval(2)
                .infinite()
                .build();
    }

    public void setGameOver() {
        this.gameLoop.setExpired();
        this.gameOver = true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Moves the player's 'ship' one unit in a given direction.
     *
     * @param direction
     */
    public void movePlayer(Direction direction) {
        int currentCol = player.getCol();
        if (direction == Direction.LEFT) {
            player.setCol(Math.max(currentCol - 1, 0));
        } else {
            player.setCol(Math.min(currentCol + 1, 8));
        }
        update();
    }

    /**
     * Creates a projectile originating from the player 'ship'.
     */
    public void shoot() {
        projectiles.add(new ShooterProjectile(player.getCol()));
        update();
    }

    public PlayerShip getPlayer() {
        return player;
    }

    public List<ShooterProjectile> getProjectiles() {
        return projectiles;
    }

    public List<ShooterEnemy> getEnemies() {
        return enemies;
    }

    /**
     * Sets the health of the player.
     * If health is set below or equal to 0, the game ends.
     *
     * @param health
     */
    public void setHealth(int health) {
        this.health = health;
        this.healthDirty = true;
        if (health <= 0) {
            setGameOver();
        }
        update();
    }

    public boolean isHealthDirty() {
        return this.healthDirty;
    }

    public void setHealthClean() {
        this.healthDirty = false;
    }

    public int getHealth() {
        return health;
    }

    /**
     * Main logic for the game. This is consistently repeated until the game is over.
     */
    private void loop() {
        this.timesLooped++;
        // Spawn between 0-4 enemies once every 24 loops
        if (timesLooped % 24 == 0) {
            for (int i = 0; i < random.nextInt(4); i++)
                spawnEnemy();
        }
        // Move all enemies forward one block every 6 loops
        if (timesLooped % 6 == 0) {
            moveAllEnemies();
        }
        // Moves all projectiles up
        moveAllProjectiles();
        update();
    }

    /**
     * Moves all enemies up one block. If an enemy hits the block under the player ship,
     * it deducts 1 health and kills the enemy.
     */
    private void moveAllEnemies() {
        for (ShooterEnemy enemy : Lists.newArrayList(enemies)) {
            enemy.move();
            if (enemy.getRow() >= 8) {
                this.setHealth(this.getHealth() - 1);
                this.enemies.remove(enemy);
            }
        }
        update();
    }

    /**
     * Moves all projectiles on block. If a projectile hits an enemy, damage the enemy and kill the projectile.
     */
    private void moveAllProjectiles() {
        for (ShooterProjectile projectile : Lists.newArrayList(projectiles)) {
            projectile.move();
            if (projectile.getRow() < 0) {
                this.projectiles.remove(projectile);
                continue;
            }
            getEnemyAt(projectile.getRow(), projectile.getCol()).ifPresent((enemy) -> {
                damageEnemy(enemy);
                this.projectiles.remove(projectile);
            });
        }
    }

    /**
     * Attempts to spawn an enemy in a random column at the top of the grid.
     */
    private void spawnEnemy() {
        int passes = 0;
        while (passes < 20) {
            passes++;
            int col = random.nextInt(9);
            if (!getEnemyAt(0, col).isPresent()) {
                this.enemies.add(getRandomEnemy(col));
                update();
                return;
            }
        }
    }

    /**
     * Gets a new instance of a random enemy type.
     *
     * @param col column to spawn enemy at
     * @return new instance of random enemy type
     */
    private ShooterEnemy getRandomEnemy(int col) {
        switch (random.nextInt(3)) {
            case 0:
                return new SmallAsteroid(col);
            case 1:
                return new MediumAsteroid(col);
            case 2:
                return new LargeAsteroid(col);
        }
        return null;
    }

    private void damageEnemy(ShooterEnemy enemy) {
        enemy.damage();
        if (enemy.getHealth() <= 0) {
            this.enemies.remove(enemy);
        }
    }

    /**
     * Gets an enemy at a given cell, if present
     *
     * @param row the row to get enemy at
     * @param col the column to get enemy at
     * @return optional containing enemy if present, empty if not present
     */
    private Optional<ShooterEnemy> getEnemyAt(int row, int col) {
        for (ShooterEnemy enemy : Lists.newArrayList(enemies)) {
            if (enemy.getRow() == row && enemy.getCol() == col)
                return Optional.of(enemy);
        }
        return Optional.empty();
    }

    public enum Direction {
        LEFT,
        RIGHT
    }

}
