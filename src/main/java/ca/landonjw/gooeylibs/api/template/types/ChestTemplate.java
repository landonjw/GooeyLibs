package ca.landonjw.gooeylibs.api.template.types;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.template.LineType;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.TemplateType;
import ca.landonjw.gooeylibs.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChestTemplate extends Template {

    public ChestTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.CHEST, slots);
    }

    public int getRows() {
        return getSize() / 9;
    }

    public TemplateSlot getSlot(int row, int col) {
        return getSlot(row * 9 + col);
    }

    public static Builder builder(int rows) {
        return new Builder(rows);
    }

    @Override
    public ChestTemplate clone() {
        TemplateSlot[] clonedSlots = new TemplateSlot[getSize()];
        for (int i = 0; i < getSize(); i++) {
            int row = i / 9;
            int col = i % 9;

            Button button = getSlot(i).getButton().orElse(null);
            clonedSlots[i] = new TemplateSlot(button, col + row * 9, 8 + col * 18, 18 + row * 18);
        }
        return new ChestTemplate(clonedSlots);
    }

    public static class Builder {

        private final int COLUMNS = 9;
        private final int rows;
        private final Button[] buttons;

        protected Button get(int row, int col) {
            return buttons[row * 9 + col];
        }

        protected Button get(int index) {
            return buttons[index];
        }

        public Builder(int rows) {
            this.rows = rows;
            this.buttons = new Button[rows * COLUMNS];
        }

        public Builder set(int index, @Nullable Button button) {
            this.buttons[index] = button;
            return this;
        }

        public Builder set(int row, int col, @Nullable Button button) {
            return set(row * 9 + col, button);
        }

        public Builder row(int row, @Nullable Button button) {
            if (row < 0 || row >= rows) return this;
            for (int col = 0; col < COLUMNS; col++) {
                set(row, col, button);
            }
            return this;
        }

        public Builder column(int col, @Nullable Button button) {
            if (col < 0 || col >= COLUMNS) return this;
            for (int row = 0; row < rows; row++) {
                set(row, col, button);
            }
            return this;
        }

        public Builder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
            if (lineType == LineType.HORIZONTAL) {
                if (startRow < 0 || startRow > rows) return this;

                int endCol = startCol + length - 1;
                for (int col = Math.max(0, startCol); col <= Math.min(COLUMNS, endCol); col++) {
                    set(startRow, col, button);
                }
            } else {
                if (startCol < 0 || startCol > COLUMNS) return this;

                int endRow = startRow + length - 1;
                for (int row = Math.max(0, startRow); row <= Math.min(rows, endRow); row++) {
                    set(row, startCol, button);
                }
            }
            return this;
        }

        public Builder square(int startRow, int startCol, int size, @Nullable Button button) {
            return rectangle(startRow, startCol, size, size, button);
        }

        public Builder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
            startRow = Math.max(0, startRow);
            startCol = Math.max(0, startCol);
            int endRow = Math.min(rows, startRow + length);
            int endCol = Math.min(COLUMNS, startCol + width);

            for (int row = startRow; row < endRow; row++) {
                for (int col = startCol; col < endCol; col++) {
                    set(row, col, button);
                }
            }
            return this;
        }

        public Builder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
            startRow = Math.max(0, startRow);
            startCol = Math.max(0, startCol);
            int endRow = Math.min(rows, startRow + length);
            int endCol = Math.min(COLUMNS, startCol + width);

            for (int row = startRow; row < endRow; row++) {
                set(row, startCol, button);
                set(row, endCol - 1, button);
            }
            for (int col = startCol; col < endCol; col++) {
                set(startRow, col, button);
                set(endRow - 1, col, button);
            }
            return this;
        }

        public Builder checker(int startRow, int startCol, int length, int width, @Nullable Button button, @Nullable Button button2) {
            startRow = Math.max(0, startRow);
            startCol = Math.max(0, startCol);
            int endRow = Math.min(rows, startRow + length);
            int endCol = Math.min(COLUMNS, startCol + width);

            for (int row = startRow; row < endRow; row++) {
                for (int col = startCol; col < endCol; col++) {
                    if (row - col == 0 || (row - col) % 2 == 0) {
                        set(row, col, button);
                    } else {
                        set(row, col, button2);
                    }
                }
            }
            return this;
        }

        public Builder fill(@Nullable Button button) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    if (get(row, col) == null) {
                        set(row, col, button);
                    }
                }
            }
            return this;
        }

        public ChestTemplate build() {
            return new ChestTemplate(toSlots());
        }

        protected TemplateSlot[] toSlots() {
            TemplateSlot[] slots = new TemplateSlot[buttons.length];
            for (int i = 0; i < buttons.length; i++) {
                int row = i / 9;
                int col = i % 9;

                slots[i] = new TemplateSlot(buttons[i], col + row * 9, 8 + col * 18, 18 + row * 18);
            }
            return slots;
        }

    }

}
