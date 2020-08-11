package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.button.Button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Template {

	Button[][] getButtons();

	int getRows();

	Template clone();

	static TemplateBuilder builder(int rows) {
		return new BaseTemplate.BaseTemplateBuilder(rows);
	}

	interface TemplateBuilder {

		TemplateBuilder set(int row, int col, @Nullable Button button);

		TemplateBuilder set(int slot, Button button);

		TemplateBuilder row(int row, @Nullable Button button);

		TemplateBuilder column(int col, @Nullable Button button);

		TemplateBuilder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button);

		TemplateBuilder square(int startRow, int startCol, int size, @Nullable Button button);

		TemplateBuilder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button);

		TemplateBuilder border(int startRow, int startCol, int length, int width, @Nullable Button button);

		TemplateBuilder checker(int startRow, int startCol, int length, int width, @Nullable Button button);

		TemplateBuilder fill(@Nullable Button button);

		MultiButtonFiller toMultiButtonFiller();

		TemplateBuilder reset();

		Template build();

	}

	interface MultiButtonFiller {

		MultiButtonFiller row(int row, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller column(int col, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller square(int startRow, int startCol, int size, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller rectangle(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller border(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller checker(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons);

		MultiButtonFiller fill(@Nonnull Iterable<Button> buttons);

		TemplateBuilder toTemplateBuilder();

	}

}
