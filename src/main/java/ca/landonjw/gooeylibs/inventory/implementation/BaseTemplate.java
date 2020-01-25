package ca.landonjw.gooeylibs.inventory.implementation;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.LineType;
import ca.landonjw.gooeylibs.inventory.api.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base implementation of {@link Template}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BaseTemplate implements Template {

    /** The number of columns in any template. */
    private static final int NUM_COLUMNS = 9;
    /** The number of rows in the template. */
    private int rows;
    /** A two dimensional array containing all buttons in each corresponding row and column. */
    private Button[][] buttons;

    /**
     * Constructor for a template.
     *
     * @param rows    the number of rows in the template
     * @param buttons the buttons in the template
     * @throws IllegalArgumentException if number of rows is below or equal to 0
     * @throws NullPointerException     if two dimensional button array is null
     */
    protected BaseTemplate(int rows, @Nonnull Button[][] buttons){
        if(rows <= 0){
            throw new IllegalArgumentException("template must not have less than 1 row");
        }
        this.rows = rows;
        this.buttons = Objects.requireNonNull(buttons, "buttons must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public Button[][] getButtons() {
        return buttons;
    }

    /** {@inheritDoc} */
    @Override
    public int getRows() {
        return rows;
    }

    /** {@inheritDoc} */
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /** {@inheritDoc} */
    @Override
    public Template clone(){
        return new BaseTemplate(rows, cloneButtons(buttons));
    }

    /**
     * Clones all buttons in the template. Clones the buttons by {@link Button#clone()}.
     *
     * @param buttons buttons to clone
     * @return a clone of all buttons
     */
    private static Button[][] cloneButtons(Button[][] buttons){
        Button[][] clone = new Button[buttons.length][9];
        for(int row = 0; row < buttons.length; row++){
            for(int col = 0; col < NUM_COLUMNS; col++){
                if(buttons[row][col] != null){
                    clone[row][col] = buttons[row][col].clone();
                }
            }
        }
        return clone;
    }

    /**
     * Base implementation of {@link Template.Builder}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    public static class Builder implements Template.Builder {

        /** The number of rows in the template. */
        private int rows;
        /** A two dimensional array containing all buttons in each corresponding row and column. */
        private Button[][] buttons;

        /**
         * Constructor for a builder
         *
         * @param rows the number of rows in the template
         * @throws IllegalArgumentException if number of rows is below or equal to 0
         */
        public Builder(int rows){
            if(rows <= 0){
                throw new IllegalArgumentException("template must not have less than 1 row");
            }
            this.rows = rows;
            buttons = new Button[rows][9];
        }

        /**
         * Constructor for a builder, taking a {@link Template} argument.
         * This is intended for functionality of {@link Template#toBuilder()}.
         *
         * @param template the template to create builder from
         */
        public Builder(BaseTemplate template){
            this.buttons = template.buttons;
            this.rows = template.rows;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder set(int row, int col, @Nullable Button button) {
            if(row >= 0 && row < rows && col >= 0 && col < NUM_COLUMNS){
                buttons[row][col] = button;
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder row(int row, @Nullable Button button) {
            if(row >= 0 && row < rows){
                for(int col = 0; col < NUM_COLUMNS; col++){
                    buttons[row][col] = button;
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder column(int col, @Nullable Button button) {
            if(col >= 0 && col < NUM_COLUMNS){
                for(int row = 0; row < rows; row++){
                    buttons[row][col] = button;
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
            Objects.requireNonNull(lineType, "line type must not be null");
            if(lineType == LineType.Horizontal){
                int endCol = startCol + length;
                if(startRow >= 0 && startRow < rows){
                    for(int col = startCol; col < endCol; col++){
                        if(col >= 0 && col < NUM_COLUMNS){
                            buttons[startRow][col] = button;
                        }
                    }
                }
            }
            else if(lineType == LineType.Vertical){
                int endRow = startRow + length;
                if(startCol >= 0 && startCol < NUM_COLUMNS){
                    for(int row = startRow; row < endRow; row++){
                        if(row >= 0 && row < rows){
                            buttons[row][startCol] = button;
                        }
                    }
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder square(int startRow, int startCol, int size, @Nullable Button button) {
            return rectangle(startRow, startCol, size, size, button);
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
            int endRow = startRow + length;
            int endCol = startCol + width;

            for(int row = startRow; row < endRow && row < rows; row++){
                for(int col = startCol; col < endCol && col < NUM_COLUMNS; col++){
                    if(row >= 0 && col >= 0){
                        buttons[row][col] = button;
                    }
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
            int endRow = startRow + (length - 1);
            int endCol = startCol + (width - 1);

            for(int row = startRow; row <= endRow && row < rows; row++){
                if(row >= 0){
                    if(startCol >= 0 && startCol < NUM_COLUMNS){
                        buttons[row][startCol] = button;
                    }
                    if(endCol >= 0 && endCol < NUM_COLUMNS){
                        buttons[row][endCol] = button;
                    }
                }
            }
            for(int col = startCol; col <= endCol && col < NUM_COLUMNS; col++){
                if(col >= 0){
                    if(startRow >= 0 && startRow < rows){
                        buttons[startRow][col] = button;
                    }
                    if(endRow >= 0 && endRow < rows){
                        buttons[endRow][col] = button;
                    }
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder fill(@Nullable Button button) {
            for(int row = 0; row < rows; row++){
                for(int col = 0; col < NUM_COLUMNS; col++){
                    if(buttons[row][col] == null){
                        buttons[row][col] = button;
                    }
                }
            }
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template.Builder reset() {
            buttons = new Button[buttons.length][9];
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public Template build() {
            return new BaseTemplate(rows, cloneButtons(buttons));
        }

    }

}
