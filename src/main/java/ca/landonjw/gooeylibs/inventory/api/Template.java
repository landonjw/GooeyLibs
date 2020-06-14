package ca.landonjw.gooeylibs.inventory.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for a template that is displayed on a {@link Page}.
 *
 * <p>This contains the overall layout of a page, and allows for the addition
 * of {@link Button}s to make a page interactive.</p>
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface Template {

    /**
     * Gets the {@link Button}s that are contained within the template.
     *
     * <p>The buttons are contained within a two dimensional array corresponding
     * to the rows and columns on the template. Any row or column that does not
     * have a button will be null.</p>
     *
     * @return a two dimensional array containing all buttons in the template
     */
    Button[][] getButtons();

    /**
     * Gets the number of rows in the template.
     *
     * @return the number of rows in the template
     */
    int getRows();

    /**
     * Returns a new {@link Builder} with the contents of the template.
     * This can be used to edit the otherwise immutable template instance.
     *
     * @return a new template builder with contents of the template
     */
    Builder toBuilder();

    /**
     * Clones the template to create a new template with identical contents.
     * This produces a deep copy, changes to the copied template will not be reflected in the original.
     *
     * @return a clone of the template
     */
    Template clone();

    /**
     * Returns a new {@link Builder} for a template.
     *
     * @param rows the number of rows in the template
     * @return a new builder for a template
     * @throws IllegalArgumentException if number of rows is below or equal to 0
     */
    static Builder builder(int rows){
        return InventoryAPI.getInstance().templateBuilder(rows);
    }

    /**
     * Interface for a builder that creates a {@link Template}.
     *
     * @author landonjw
     * @since  1.0.0
     */
    interface Builder {

        /**
         * Sets a button at a position on the template.
         *
         * @param row    the row to set button in
         * @param col    the column to set button in
         * @param button the button to set, null to remove any existing button at location
         * @return builder with button set
         */
        Builder set(int row, int col, @Nullable Button button);

        /**
         * Fills a row on the template with a button.
         *
         * <p>An example usage of this is {@code row(0, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in the first row in the template to be filled with the diamond button.</p>
         *
         * @param row    the row to fill with button
         * @param button the button to fill row with, null to remove any existing buttons in row
         * @return builder with row filled from button
         */
        Builder row(int row, @Nullable Button button);

        /**
         * Fills a row on the template with an iterable collection of buttons.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param row     the row to fill with button
         * @param buttons the iterable collection of buttons to fill row with
         * @return builder with row filled from button
         */
        Builder flexRow(int row, @Nonnull Iterable<Button> buttons);

        /**
         * Fills a column on the template with a button.
         *
         * <p>An example usage of this is {@code column(0, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in the first column in the template to be filled with the diamond button.</p>
         *
         * @param column the column to fill with button
         * @param button the button to fill column with, null to remove any existing buttons in column
         * @return builder with column filled from button
         */
        Builder column(int column, @Nullable Button button);

        /**
         * Fills a column on the template with an iterable collection of buttons.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param column  the column to fill with button
         * @param buttons the iterable collection of buttons to fill column with
         * @return builder with column filled from button
         */
        Builder flexColumn(int column, @Nonnull Iterable<Button> buttons);

        /**
         * Creates a line of buttons in the template.
         *
         * <p>If the line length will go over the page bounds, the line will stop.
         * If line type is vertical, it will draw from top to bottom.
         * If line type is horizontal, it will draw from left to right.</p>
         *
         * <p>An example usage of this is {@code line(LineType.Vertical, 0, 0, 3, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in a line three buttons long starting at the first inventory slot, created from the diamond button.</p>
         *
         * @param lineType    the type of line to create, vertical or horizontal
         * @param startRow    the row to start line at
         * @param startColumn the column to start line at
         * @param length      the length of the line
         * @param button      the button to create line with
         * @return builder with line created from button
         */
        Builder line(@Nonnull LineType lineType, int startRow, int startColumn, int length, @Nullable Button button);

        /**
         * Creates a line of buttons in the template.
         *
         * <p>If the line length will go over the page bounds, the line will stop.
         * If line type is vertical, it will draw from top to bottom.
         * If line type is horizontal, it will draw from left to right.</p>
         *
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         *
         * @param lineType     the type of line to create, vertical or horizontal
         * @param startRow     the row to start line at
         * @param startColumn  the column to start line at
         * @param length       the length of the line
         * @param buttons      the iterable collection of buttons to create line from
         * @return builder with line created from button
         */
        Builder flexLine(@Nonnull LineType lineType, int startRow, int startColumn, int length, @Nonnull Iterable<Button> buttons);

        /**
         * Creates a square of buttons in the template.
         *
         * If the square will go out of bounds, it will instead stop at those bounds.
         *
         * <p>An example usage of this is {@code rectangle(0, 0, 3, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in a filled square 3 buttons long and 3 buttons wide starting at the first inventory slot,
         * created from the diamond button.</p>
         *
         * @param startRow    the row of the top left corner of the square
         * @param startColumn the column of the top left corner of the square
         * @param size        the size of the square
         * @param button      the button to create square from
         * @return builder with square created from button
         */
        Builder square(int startRow, int startColumn, int size, @Nullable Button button);

        /**
         * Creates a square of buttons in the template.
         *
         * If the square will go out of bounds, it will instead stop at those bounds.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param startRow     the row of the top left corner of the square
         * @param startColumn  the column of the top left corner of the square
         * @param size         the size of the square
         * @param buttons      the iterable collection of buttons to create square from
         * @return builder with square created from button
         */
        Builder flexSquare(int startRow, int startColumn, int size, @Nonnull Iterable<Button> buttons);

        /**
         * Creates a rectangle of buttons in the template.
         *
         * If the rectangle will go out of bounds, it will instead stop at those bounds.
         *
         * <p>An example usage of this is {@code rectangle(0, 0, 6, 9, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in a filled rectangle 6 buttons long and 9 buttons wide starting at the first inventory slot,
         * created from the diamond button.</p>
         *
         * @param startRow    the row of the top left corner of the rectangle
         * @param startColumn the column of the top left corner of the rectangle
         * @param length      the length of the rectangle
         * @param width       the width of the rectangle
         * @param button      the button to create rectangle from
         * @return builder with rectangle created from button
         */
        Builder rectangle(int startRow, int startColumn, int length, int width, @Nullable Button button);

        /**
         * Creates a rectangle of buttons in the template.
         *
         * If the rectangle will go out of bounds, it will instead stop at those bounds.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param startRow     the row of the top left corner of the rectangle
         * @param startColumn  the column of the top left corner of the rectangle
         * @param length       the length of the rectangle
         * @param width        the width of the rectangle
         * @param buttons      the iterable collection of buttons to create rectangle from
         * @return builder with rectangle created from button
         */
        Builder flexRectangle(int startRow, int startColumn, int length, int width, @Nonnull Iterable<Button> buttons);

        /**
         * Creates a border of buttons in the template.
         * Only the perimeter of the defined area is filled.
         *
         * <p>An example usage of this is {@code border(0, 0, 6, 9, Button.of(new ItemStack(Items.DIAMOND)))},
         * which will result in a border 6 buttons long and 9 buttons wide starting at the first inventory slot,
         * created from the diamond button.</p>
         *
         * @param startRow    the row of the top left corner of the border
         * @param startColumn the column of the top left corner of the border
         * @param length      the length of the border
         * @param width       the width of the border
         * @param button      the button to create border from
         * @return builder with border created from button
         */
        Builder border(int startRow, int startColumn, int length, int width, @Nullable Button button);

        /**
         * Creates a border of buttons in the template.
         * Only the perimeter of the defined area is filled.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param startRow     the row of the top left corner of the border
         * @param startColumn  the column of the top left corner of the border
         * @param length       the length of the border
         * @param width        the width of the border
         * @param buttons      the iterable collection of buttons to create border from
         * @return builder with border created from button
         */
        Builder flexBorder(int startRow, int startColumn, int length, int width, @Nonnull Iterable<Button> buttons);

        /**
         * Fills any empty slots in the template with a button.
         * This will not overwrite any non-empty slots.
         *
         * <p>An example usage of this is {@code fill(Button.of(new ItemStack(Items.DIAMOND)))}, which will
         * result any empty slots in the template to be filled with diamonds.</p>
         *
         * @param button the button to fill template with
         * @return builder with empty slots filled with button
         */
        Builder fill(@Nullable Button button);

        /**
         * Fills any empty slots in the template with a button.
         * This will not overwrite any non-empty slots.
         * If the iterable collection of buttons run out before all slots are filled, the remaining slots will remain empty.
         *
         * @param buttons the iterable collection of buttons to fill template with
         * @return builder with empty slots filled with button
         */
        Builder flexFill(@Nonnull Iterable<Button> buttons);

        /**
         * Creates a checker board alternating between two {@link Button}s between supplied dimensions.
         *
         * <p>An example usage of this is {@code checker(0, 0, 6, 9, Button.of(new ItemStack(Items.DIAMOND)), Button.of(new ItemStack(Items.EMERALD)))},
         * which will result in a checker pattern starting at the first inventory slot,spreading 6 rows and 9 columns,
         * where diamond is on the even slots and emerald is on the odd slots.</p>
         *
         * @param startRow the row of the top left corner of the border
         * @param startCol the column of the top left corner of the border
         * @param length   the length of the border
         * @param width    the width of the border
         * @param even     the button to put on even squares
         * @param odd      the button to put on off squares
         * @return builder with checker pattern created from buttons
         */
        Builder checker(int startRow, int startCol, int length, int width, @Nullable Button even, @Nullable Button odd);

        /**
         * Creates a checker board alternating between two iterable collections of {@link Button}s between supplied dimensions.
         * If the iterable collections of buttons run out before the dimensions are filled, the remaining slots will be empty.
         *
         * @param startRow the row of the top left corner of the border
         * @param startCol the column of the top left corner of the border
         * @param length   the length of the border
         * @param width    the width of the border
         * @param even     the iterable collection of buttons to put on even squares
         * @param odd      the iterable collection of buttons to put on off squares
         * @return builder with checker pattern created from buttons
         */
        Builder flexChecker(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> even, @Nonnull Iterable<Button> odd);

        /**
         * Resets the builder to it's default state.
         *
         * @return the builder with values reset
         */
        Builder reset();

        /**
         * Builds a new {@link Template} instance from values in the builder.
         *
         * @return new template instance
         */
        Template build();

    }

}
