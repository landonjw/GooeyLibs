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
         * @param row    the row to fill with button
         * @param button the button to fill row with, null to remove any existing buttons in row
         * @return builder with row filled from button
         */
        Builder row(int row, @Nullable Button button);

        /**
         * Fills a row on the template with a button.
         *
         * @param column    the column to fill with button
         * @param button the button to fill column with, null to remove any existing buttons in column
         * @return builder with column filled from button
         */
        Builder column(int column, @Nullable Button button);

        /**
         * Creates a line of buttons in the template.
         *
         * <p>If the line length will go over the page bounds, the line will stop.
         * If line type is vertical, it will draw from top to bottom.
         * If line type is horizontal, it will draw from left to right.</p>
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
         * Creates a square of buttons in the template.
         *
         * If the square will go out of bounds, it will instead stop at those bounds.
         *
         * @param startRow    the row of the top left corner of the square
         * @param startColumn the column of the top left corner of the square
         * @param size        the size of the square
         * @param button      the button to create square from
         * @return builder with square created from button
         */
        Builder square(int startRow, int startColumn, int size, @Nullable Button button);

        /**
         * Creates a rectangle of buttons in the template.
         *
         * If the rectangle will go out of bounds, it will instead stop at those bounds.
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
         * Creates a border of buttons in the template.
         * Only the perimeter of the defined rectangle is filled.
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
         * Fills any empty slots in the template with a button.
         * This will not overwrite any non-empty slots.
         *
         * @param button the button to fill template with
         * @return builder with empty slots filled with button
         */
        Builder fill(@Nullable Button button);

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
