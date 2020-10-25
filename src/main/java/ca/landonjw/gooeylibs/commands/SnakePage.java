package ca.landonjw.gooeylibs.commands;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs.api.template.types.InventoryTemplate;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SnakePage extends UpdateEmitter<Page> implements Page {

    private final ChestTemplate template;
    private final InventoryTemplate inventoryTemplate;

    private boolean started;

    private final List<Tuple<Integer, Integer>> snakeParts = Lists.newArrayList();

    private Direction direction;

    private Tuple<Integer, Integer> applePosition = null;

    private final Button snakeBall = GooeyButton.builder()
            .display(new ItemStack(Items.SLIME_BALL))
            .build();

    private final Button apple = GooeyButton.builder()
            .display(new ItemStack(Items.APPLE))
            .build();

    public SnakePage() {
        this.template = ChestTemplate.builder(6)
                .set(3, 4, snakeBall)
                .build();

        this.inventoryTemplate = InventoryTemplate.builder()
                .set(0, 4, getDirectionButton(Direction.UP))
                .set(1, 3, getDirectionButton(Direction.LEFT))
                .set(1, 5, getDirectionButton(Direction.RIGHT))
                .set(2, 4, getDirectionButton(Direction.DOWN))
                .build();

        this.snakeParts.add(new Tuple<>(3, 4));
    }

    private Button getDirectionButton(Direction direction) {
        return GooeyButton.builder()
                .display(new ItemStack(Items.SNOWBALL))
                .onClick(() -> setDirection(direction))
                .build();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (!started) startGame();
    }

    private void startGame() {
        this.started = true;
        createApple();
        Task.builder()
                .execute(() -> setNextFrame())
                .interval(10)
                .infinite()
                .build();
        addSnakePart();
    }

    private void createApple() {
        Tuple<Integer, Integer> applePosition = null;
        while (applePosition == null) {
            int randomRow = new Random().nextInt(6);
            int randomCol = new Random().nextInt(9);
            if (!template.getSlot(randomRow, randomCol).getButton().isPresent()) {
                applePosition = new Tuple<>(randomRow, randomCol);
            }
        }
        this.applePosition = applePosition;
        template.getSlot(applePosition.getFirst(), applePosition.getSecond()).setButton(apple);
    }

    private void addSnakePart() {
        Tuple<Integer, Integer> tail = snakeParts.get(snakeParts.size() - 1);
        this.snakeParts.add(tail);
    }

    private void setNextFrame() {
        Tuple<Integer, Integer> snakeHead = snakeParts.get(0);
        int newHeadRow = snakeHead.getFirst();
        int newHeadCol = snakeHead.getSecond();

        switch (direction) {
            case UP:
                newHeadRow = (newHeadRow - 1 + 6) % 6;
                break;
            case DOWN:
                newHeadRow = (newHeadRow + 1) % 6;
                break;
            case LEFT:
                newHeadCol = (newHeadCol - 1 + 9) % 9;
                break;
            case RIGHT:
                newHeadCol = (newHeadCol + 1) % 9;
                break;
        }
        Tuple<Integer, Integer> newHead = new Tuple<>(newHeadRow, newHeadCol);
        snakeParts.add(0, newHead);

        Tuple<Integer, Integer> snakeTail = snakeParts.get(snakeParts.size() - 1);
        template.getSlot(snakeTail.getFirst(), snakeTail.getSecond()).setButton(null);
        snakeParts.remove(snakeParts.size() - 1);

        for (Tuple<Integer, Integer> snakePart : snakeParts) {
            template.getSlot(snakePart.getFirst(), snakePart.getSecond()).setButton(snakeBall);
        }
        if (applePosition.getFirst() == newHeadRow && applePosition.getSecond() == newHeadCol) {
            addSnakePart();
            createApple();
        }
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public Optional<InventoryTemplate> getInventoryTemplate() {
        return Optional.of(inventoryTemplate);
    }

    @Override
    public String getTitle() {
        return "Snake Game";
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

}