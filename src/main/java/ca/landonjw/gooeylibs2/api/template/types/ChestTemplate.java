package ca.landonjw.gooeylibs2.api.template.types;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.helpers.TemplateHelper;
import ca.landonjw.gooeylibs2.api.template.LineType;
import ca.landonjw.gooeylibs2.api.template.Template;
import ca.landonjw.gooeylibs2.api.template.TemplateType;
import ca.landonjw.gooeylibs2.api.template.slot.TemplateSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChestTemplate extends Template {

    protected static final int COLUMNS = 9;

    public ChestTemplate(@Nonnull TemplateSlot[] slots) {
        super(TemplateType.CHEST, slots);
    }

    public int getRows() {
        return getSize() / 9;
    }

    public TemplateSlot getSlot(int row, int col) {
        return getSlot(row * 9 + col);
    }

    public ChestTemplate set(int index, @Nullable Button button) {
        getSlot(index).setButton(button);
        return this;
    }

    public ChestTemplate set(int row, int col, @Nullable Button button) {
        getSlot(row, col).setButton(button);
        return this;
    }

    public ChestTemplate row(int row, @Nullable Button button) {
        if (row < 0 || row >= getRows()) return this;
        for (int col = 0; col < COLUMNS; col++) {
            set(row, col, button);
        }
        return this;
    }

    public ChestTemplate column(int col, @Nullable Button button) {
        if (col < 0 || col >= COLUMNS) return this;
        for (int row = 0; row < getRows(); row++) {
            set(row, col, button);
        }
        return this;
    }

    public ChestTemplate line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
        if (lineType == LineType.HORIZONTAL) {
            if (startRow < 0 || startRow > getRows()) return this;

            int endCol = startCol + length - 1;
            for (int col = Math.max(0, startCol); col <= Math.min(COLUMNS, endCol); col++) {
                set(startRow, col, button);
            }
        } else {
            if (startCol < 0 || startCol > COLUMNS) return this;

            int endRow = startRow + length - 1;
            for (int row = Math.max(0, startRow); row <= Math.min(getRows(), endRow); row++) {
                set(row, startCol, button);
            }
        }
        return this;
    }

    public ChestTemplate square(int startRow, int startCol, int size, @Nullable Button button) {
        rectangle(startRow, startCol, size, size, button);
        return this;
    }

    public ChestTemplate rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
        startRow = Math.max(0, startRow);
        startCol = Math.max(0, startCol);
        int endRow = Math.min(getRows(), startRow + length);
        int endCol = Math.min(COLUMNS, startCol + width);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                set(row, col, button);
            }
        }
        return this;
    }

    public ChestTemplate border(int startRow, int startCol, int length, int width, @Nullable Button button) {
        startRow = Math.max(0, startRow);
        startCol = Math.max(0, startCol);
        int endRow = Math.min(getRows(), startRow + length);
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

    public ChestTemplate checker(int startRow, int startCol, int length, int width, @Nullable Button button, @Nullable Button button2) {
        startRow = Math.max(0, startRow);
        startCol = Math.max(0, startCol);
        int endRow = Math.min(getRows(), startRow + length);
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

    public ChestTemplate fill(@Nullable Button button) {
        for (int i = 0; i < getSize(); i++) {
            if (!getSlot(i).getButton().isPresent()) {
                getSlot(i).setButton(button);
            }
        }
        return this;
    }

    public ChestTemplate clear() {
        for (int slotIndex = 0; slotIndex < getSize(); slotIndex++) {
            getSlot(slotIndex).setButton(null);
        }
        return this;
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

        /**
         * Instance of the template being built.
         * <p>
         * In 2.1.0, all template builders were moved from storing data instances (ie. button arrays)
         * of templates and constructing them on build, to simply delegating to an instance of the template.
         * <p>
         * This was done in order to shift all convenience methods (ie. row, column, border, etc.) into the
         * corresponding Template classes themselves to allow for easier modification of button state after
         * the Template was built.
         * <p>
         * Since we assign a new template instance at the end of each {@link #build()},
         * this should not have any side effects and thus be backwards compatible.
         * <p>
         * Yay for abstraction!
         */
        private ChestTemplate templateInstance;
        protected int rows;

        public Builder(int rows) {
            this.rows = rows;
            this.templateInstance = new ChestTemplate(TemplateHelper.slotsOf(rows * COLUMNS));
        }

        public Builder set(int index, @Nullable Button button) {
            templateInstance.set(index, button);
            return this;
        }

        public Builder set(int row, int col, @Nullable Button button) {
            templateInstance.set(row, col, button);
            return this;
        }

        public Builder row(int row, @Nullable Button button) {
            templateInstance.row(row, button);
            return this;
        }

        public Builder column(int col, @Nullable Button button) {
            templateInstance.column(col, button);
            return this;
        }

        public Builder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
            templateInstance.line(lineType, startRow, startCol, length, button);
            return this;
        }

        public Builder square(int startRow, int startCol, int size, @Nullable Button button) {
            templateInstance.square(startRow, startCol, size, button);
            return this;
        }

        public Builder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
            templateInstance.rectangle(startRow, startCol, length, width, button);
            return this;
        }

        public Builder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
            templateInstance.border(startRow, startCol, length, width, button);
            return this;
        }

        public Builder checker(int startRow, int startCol, int length, int width, @Nullable Button button, @Nullable Button button2) {
            templateInstance.checker(startRow, startCol, length, width, button, button2);
            return this;
        }

        public Builder fill(@Nullable Button button) {
            templateInstance.fill(button);
            return this;
        }

        public ChestTemplate build() {
            ChestTemplate templateToReturn = templateInstance;
            templateInstance = new ChestTemplate(TemplateHelper.slotsOf(rows * COLUMNS));
            return templateToReturn;
        }

    }

}
