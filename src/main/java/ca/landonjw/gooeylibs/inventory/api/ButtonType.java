package ca.landonjw.gooeylibs.inventory.api;

/**
 * Classifies the type of a {@link Button}. The typical button is of type Standard, which offers
 * no additional behavior.
 *
 * Button Types:
 * <ul>
 *     <li>Standard: Offers no additional functionality</li>
 *     <li>NextPage: Opens the next linked {@link Page} if there is one.</li>
 *     <li>PreviousPage: Opens the previous linked {@link Page} if there is one.</li>
 *     <li>PageInfo: Substitutes placeholders for page attribute's to allow for describing page numbers</li>
 * </ul>
 *
 * @author landonjw
 * @since  1.0.0
 */
public enum ButtonType {
    /** The standard button type. Offers no additional functionality. */
    Standard,
    /** Links to the next page, if there is one. */
    NextPage,
    /** Links to the previous page, if there is one. */
    PreviousPage,
    /**
     * Substitutes placeholders in a button's title for attribute's of the {@link Page} it's
     * on to allow for describing page numbers.
     *
     * Current Placeholders:
     * <ul>
     *     <li>{current}: The page number of the current page the button is on.</li>
     *     <li>{total}: The total number of pages linked together.</li>
     * </ul>
     */
    PageInfo
}