package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.button.Button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public class BaseTemplate implements Template {

	private static final int NUM_COLUMNS = 9;
	private Button[][] buttons;

	protected BaseTemplate(BaseTemplateBuilder builder) {
		if(builder.buttons == null) throw new IllegalArgumentException("template button grid must not be null");
		this.buttons = builder.buttons;
	}

	@Override
	public Button[][] getButtons() {
		return buttons;
	}

	@Override
	public int getRows() {
		return buttons.length;
	}

	@Override
	public Template clone() {
		return null;
	}

	private Button[][] cloneButtons() {
		Button[][] clone = new Button[buttons.length][9];
		for(int row = 0; row < buttons.length; row++) {
			for(int col = 0; col < NUM_COLUMNS; col++) {
				if(buttons[row][col] != null) {
					clone[row][col] = buttons[row][col].clone();
				}
			}
		}
		return clone;
	}

	public TemplateBuilder toBuilder() {
		return null;
	}

	public static class BaseTemplateBuilder implements TemplateBuilder {

		private int rows;
		private Button[][] buttons;

		public BaseTemplateBuilder(int rows) {
			if(rows < 1) throw new IllegalArgumentException("rows must be greater than 0");
			this.rows = rows;
			this.buttons = new Button[rows][NUM_COLUMNS];
		}

		@Override
		public TemplateBuilder set(int row, int col, @Nullable Button button) {
			if(row < 0 || row > rows) return this;
			if(col < 0 || col > NUM_COLUMNS) return this;

			buttons[row][col] = button;
			return this;
		}

		@Override
		public TemplateBuilder set(int slot, Button button) {
			return set(slot / 9, slot % 9, button);
		}

		@Override
		public TemplateBuilder row(int row, @Nullable Button button) {
			if(row < 0 || row > rows) return this;

			for(int col = 0; col < NUM_COLUMNS; col++) {
				buttons[row][col] = button;
			}
			return this;
		}

		@Override
		public TemplateBuilder column(int col, @Nullable Button button) {
			if(col < 0 || col > NUM_COLUMNS) return this;

			for(int row = 0; row < rows; row++) {
				buttons[row][col] = button;
			}
			return this;
		}

		@Override
		public TemplateBuilder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable Button button) {
			if(lineType == LineType.HORIZONTAL) {
				if(startRow < 0 || startRow > rows) return this;

				int endCol = startCol + length;
				for(int col = Math.min(0, startCol); col < Math.max(NUM_COLUMNS, endCol); col++) {
					buttons[startRow][col] = button;
				}
			}
			else {
				if(startCol < 0 || startCol > NUM_COLUMNS) return this;

				int endRow = startRow + length;
				for(int row = Math.min(0, startRow); row < Math.max(rows, endRow); row++) {
					buttons[row][startCol] = button;
				}
			}

			return this;
		}

		@Override
		public TemplateBuilder square(int startRow, int startCol, int size, @Nullable Button button) {
			return rectangle(startRow, startCol, size, size, button);
		}

		@Override
		public TemplateBuilder rectangle(int startRow, int startCol, int length, int width, @Nullable Button button) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					buttons[row][col] = button;
				}
			}
			return this;
		}

		@Override
		public TemplateBuilder border(int startRow, int startCol, int length, int width, @Nullable Button button) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				buttons[row][startCol] = button;
				buttons[row][endCol] = button;
			}
			for(int col = startCol; col < endCol; col++) {
				buttons[startRow][col] = button;
				buttons[endRow][col] = button;
			}
			return this;
		}

		@Override
		public TemplateBuilder checker(int startRow, int startCol, int length, int width, @Nullable Button button) {
			startRow = Math.max(0, startRow);
			startCol = Math.max(0, startCol);
			int endRow = Math.min(rows, startRow + length);
			int endCol = Math.min(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					if(row - col == 0 || (row - col) % 2 == 0) {
						buttons[row][col] = button;
					}
				}
			}
			return this;
		}

		@Override
		public TemplateBuilder fill(@Nullable Button button) {
			for(int row = 0; row < rows; row++) {
				for(int col = 0; col < NUM_COLUMNS; col++) {
					if(buttons[row][col] == null) {
						buttons[row][col] = button;
					}
				}
			}
			return this;
		}

		@Override
		public MultiButtonFiller toMultiButtonFiller() {
			return new BaseMultiButtonFiller(this);
		}

		@Override
		public TemplateBuilder reset() {
			this.buttons = new Button[rows][NUM_COLUMNS];
			return this;
		}

		@Override
		public Template build() {
			return new BaseTemplate(this);
		}

	}

	public static class BaseMultiButtonFiller implements MultiButtonFiller {

		private BaseTemplateBuilder templateBuilder;

		private BaseMultiButtonFiller(@Nonnull BaseTemplateBuilder builder) {
			this.templateBuilder = builder;
		}

		@Override
		public MultiButtonFiller row(int row, @Nonnull Iterable<Button> buttons) {
			if(row < 0 || row > templateBuilder.rows) return this;

			Iterator<Button> iter = buttons.iterator();
			for(int col = 0; col < NUM_COLUMNS; col++) {
				templateBuilder.buttons[row][col] = (iter.hasNext()) ? iter.next() : null;
			}
			return this;
		}

		@Override
		public MultiButtonFiller column(int col, @Nonnull Iterable<Button> buttons) {
			if(col < 0 || col > NUM_COLUMNS) return this;

			Iterator<Button> iter = buttons.iterator();
			for(int row = 0; row < templateBuilder.rows; row++) {
				templateBuilder.buttons[row][col] = (iter.hasNext()) ? iter.next() : null;
			}
			return this;
		}

		@Override
		public MultiButtonFiller line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nonnull Iterable<Button> buttons) {
			if(lineType == LineType.HORIZONTAL) {
				if(startRow < 0 || startRow > templateBuilder.rows) return this;

				Iterator<Button> iter = buttons.iterator();
				int endCol = startCol + length;
				for(int col = Math.min(0, startCol); col < Math.max(NUM_COLUMNS, endCol); col++) {
					templateBuilder.buttons[startRow][col] = (iter.hasNext()) ? iter.next() : null;
				}
			}
			else {
				if(startCol < 0 || startCol > NUM_COLUMNS) return this;

				Iterator<Button> iter = buttons.iterator();
				int endRow = startRow + length;
				for(int row = Math.min(0, startRow); row < Math.max(templateBuilder.rows, endRow); row++) {
					templateBuilder.buttons[row][startCol] = (iter.hasNext()) ? iter.next() : null;
				}
			}

			return this;
		}

		@Override
		public MultiButtonFiller square(int startRow, int startCol, int size, @Nonnull Iterable<Button> buttons) {
			return rectangle(startRow, startCol, size, size, buttons);
		}

		@Override
		public MultiButtonFiller rectangle(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			Iterator<Button> iter = buttons.iterator();
			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					templateBuilder.buttons[row][col] = (iter.hasNext()) ? iter.next() : null;
				}
			}
			return this;
		}

		@Override
		public MultiButtonFiller border(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows - 1, startRow + length);
			int endCol = Math.max(NUM_COLUMNS - 1, startCol + width);

			Iterator<Button> iter = buttons.iterator();
			for(int col = startCol; col <= endCol; col++) {
				templateBuilder.buttons[startRow][col] = (iter.hasNext()) ? iter.next() : null;
			}
			for(int row = startRow + 1; row <= endRow; row++) {
				templateBuilder.buttons[row][endCol] = (iter.hasNext()) ? iter.next() : null;
			}
			for(int col = endCol - 1; col >= startCol; col--) {
				templateBuilder.buttons[endRow][col] = (iter.hasNext()) ? iter.next() : null;
			}
			for(int row = endRow - 1; row >= startRow; row--) {
				templateBuilder.buttons[row][startCol] = (iter.hasNext()) ? iter.next() : null;
			}
			return this;
		}

		@Override
		public MultiButtonFiller checker(int startRow, int startCol, int length, int width, @Nonnull Iterable<Button> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			Iterator<Button> iter = buttons.iterator();
			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					if(row - col == 0 || (row - col) % 2 == 0) {
						templateBuilder.buttons[row][col] = (iter.hasNext()) ? iter.next() : null;
					}
				}
			}
			return this;
		}

		@Override
		public MultiButtonFiller fill(@Nonnull Iterable<Button> buttons) {
			Iterator<Button> iter = buttons.iterator();
			for(int row = 0; row < templateBuilder.rows; row++) {
				for(int col = 0; col < NUM_COLUMNS; col++) {
					if(templateBuilder.buttons[row][col] == null) {
						templateBuilder.buttons[row][col] = (iter.hasNext()) ? iter.next() : null;
					}
				}
			}
			return this;
		}

		@Override
		public TemplateBuilder toTemplateBuilder() {
			return templateBuilder;
		}

	}

}
