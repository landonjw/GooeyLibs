package ca.landonjw.gooeylibs.api.template;

import ca.landonjw.gooeylibs.api.button.IButton;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Template implements ITemplate {

	private static final int NUM_COLUMNS = 9;

	private List<IButton> buttons;

	protected Template(@Nonnull TemplateBuilder builder) {
		if(builder.buttons == null) throw new IllegalArgumentException("template button grid must not be null");
		this.buttons = builder.buttons;
	}

	@Override
	public int getSlots() {
		return buttons.size();
	}

	@Override
	public Optional<IButton> getButton(int row, int col) {
		return Optional.ofNullable(buttons.get(row * 9 + col));
	}

	@Override
	public Optional<IButton> getButton(int index) {
		return Optional.ofNullable(buttons.get(index));
	}

	@Override
	public NonNullList<ItemStack> toContainerDisplay() {
		return buttons.stream()
				.map((button) -> (button != null) ? button.getDisplay() : ItemStack.EMPTY)
				.collect(Collectors.toCollection(NonNullList::create));
	}

	@Override
	public Template clone() {
		return new TemplateBuilder(this).build();
	}

	public TemplateBuilder toBuilder() {
		return new TemplateBuilder(this);
	}

	public static TemplateBuilder builder(int rows) {
		return new TemplateBuilder(rows);
	}

	public static class TemplateBuilder {

		private List<IButton> buttons;
		private int rows;

		protected TemplateBuilder(int rows) {
			if(rows < 1) throw new IllegalArgumentException("rows must be greater than 0");
			this.rows = rows;
			this.buttons = new ArrayList<>();
			for(int i = 0; i < rows * 9; i++) {
				buttons.add(null);
			}
		}

		public TemplateBuilder(ITemplate template) {
			this.rows = template.getSlots() / 9;

			this.buttons = Lists.newArrayList();
			for(int i = 0; i < template.getSlots(); i++) {
				if(template.getButton(i).isPresent()) {
					this.buttons.add(template.getButton(i).get().clone());
				}
				else {
					this.buttons.add(null);
				}
			}
		}

		protected IButton get(int row, int col) {
			return buttons.get(row * 9 + col);
		}

		protected IButton get(int index) {
			return buttons.get(index);
		}

		public TemplateBuilder set(int row, int col, @Nullable IButton button) {
			if(row < 0 || row > rows) return this;
			if(col < 0 || col > NUM_COLUMNS) return this;

			buttons.set(row * 9 + col, button);
			return this;
		}

		public TemplateBuilder set(int slot, IButton button) {
			return set(slot / 9, slot % 9, button);
		}

		public TemplateBuilder row(int row, @Nullable IButton button) {
			if(row < 0 || row > rows) return this;

			for(int col = 0; col < NUM_COLUMNS; col++) {
				set(row, col, button);
			}
			return this;
		}

		public TemplateBuilder column(int col, @Nullable IButton button) {
			if(col < 0 || col > NUM_COLUMNS) return this;

			for(int row = 0; row < rows; row++) {
				set(row, col, button);
			}
			return this;
		}

		public TemplateBuilder line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nullable IButton button) {
			if(lineType == LineType.HORIZONTAL) {
				if(startRow < 0 || startRow > rows) return this;

				int endCol = startCol + length;
				for(int col = Math.min(0, startCol); col < Math.max(NUM_COLUMNS, endCol); col++) {
					set(startRow, col, button);
				}
			}
			else {
				if(startCol < 0 || startCol > NUM_COLUMNS) return this;

				int endRow = startRow + length;
				for(int row = Math.min(0, startRow); row < Math.max(rows, endRow); row++) {
					set(row, startCol, button);
				}
			}

			return this;
		}

		public TemplateBuilder square(int startRow, int startCol, int size, @Nullable IButton button) {
			return rectangle(startRow, startCol, size, size, button);
		}

		public TemplateBuilder rectangle(int startRow, int startCol, int length, int width, @Nullable IButton button) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					set(row, col, button);
				}
			}
			return this;
		}

		public TemplateBuilder border(int startRow, int startCol, int length, int width, @Nullable IButton button) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				set(row, startCol, button);
				set(row, endCol, button);
			}
			for(int col = startCol; col < endCol; col++) {
				set(startRow, col, button);
				set(endRow, col, button);
			}
			return this;
		}

		public TemplateBuilder checker(int startRow, int startCol, int length, int width, @Nullable IButton button) {
			startRow = Math.max(0, startRow);
			startCol = Math.max(0, startCol);
			int endRow = Math.min(rows, startRow + length);
			int endCol = Math.min(NUM_COLUMNS, startCol + width);

			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					if(row - col == 0 || (row - col) % 2 == 0) {
						set(row, col, button);
					}
				}
			}
			return this;
		}

		public TemplateBuilder fill(@Nullable IButton button) {
			for(int row = 0; row < rows; row++) {
				for(int col = 0; col < NUM_COLUMNS; col++) {
					if(get(row, col) == null) {
						set(row, col, button);
					}
				}
			}
			return this;
		}

		public MultiButtonFiller toMultiButtonFiller() {
			return new MultiButtonFiller(this);
		}

		public TemplateBuilder reset() {
			this.buttons = new ArrayList<>(rows * 9);
			return this;
		}

		public Template build() {
			return new Template(this);
		}

	}

	public static class MultiButtonFiller {

		private TemplateBuilder templateBuilder;

		protected MultiButtonFiller(@Nonnull TemplateBuilder builder) {
			this.templateBuilder = builder;
		}

		public MultiButtonFiller row(int row, @Nonnull Iterable<IButton> buttons) {
			if(row < 0 || row > templateBuilder.rows) return this;

			Iterator<IButton> iter = buttons.iterator();
			for(int col = 0; col < NUM_COLUMNS; col++) {
				templateBuilder.set(row, col, (iter.hasNext()) ? iter.next() : null);
			}
			return this;
		}

		public MultiButtonFiller column(int col, @Nonnull Iterable<IButton> buttons) {
			if(col < 0 || col > NUM_COLUMNS) return this;

			Iterator<IButton> iter = buttons.iterator();
			for(int row = 0; row < templateBuilder.rows; row++) {
				templateBuilder.set(row, col, (iter.hasNext()) ? iter.next() : null);
			}
			return this;
		}

		public MultiButtonFiller line(@Nonnull LineType lineType, int startRow, int startCol, int length, @Nonnull Iterable<IButton> buttons) {
			if(lineType == LineType.HORIZONTAL) {
				if(startRow < 0 || startRow > templateBuilder.rows) return this;

				Iterator<IButton> iter = buttons.iterator();
				int endCol = startCol + length;
				for(int col = Math.min(0, startCol); col < Math.max(NUM_COLUMNS, endCol); col++) {
					templateBuilder.set(startRow, col, (iter.hasNext()) ? iter.next() : null);
				}
			}
			else {
				if(startCol < 0 || startCol > NUM_COLUMNS) return this;

				Iterator<IButton> iter = buttons.iterator();
				int endRow = startRow + length;
				for(int row = Math.min(0, startRow); row < Math.max(templateBuilder.rows, endRow); row++) {
					templateBuilder.set(row, startCol, (iter.hasNext()) ? iter.next() : null);
				}
			}

			return this;
		}

		public MultiButtonFiller square(int startRow, int startCol, int size, @Nonnull Iterable<IButton> buttons) {
			return rectangle(startRow, startCol, size, size, buttons);
		}

		public MultiButtonFiller rectangle(int startRow, int startCol, int length, int width, @Nonnull Iterable<IButton> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			Iterator<IButton> iter = buttons.iterator();
			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					templateBuilder.set(row, col, (iter.hasNext()) ? iter.next() : null);
				}
			}
			return this;
		}

		public MultiButtonFiller border(int startRow, int startCol, int length, int width, @Nonnull Iterable<IButton> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows - 1, startRow + length);
			int endCol = Math.max(NUM_COLUMNS - 1, startCol + width);

			Iterator<IButton> iter = buttons.iterator();
			for(int col = startCol; col <= endCol; col++) {
				templateBuilder.set(startRow, col, (iter.hasNext()) ? iter.next() : null);
			}
			for(int row = startRow + 1; row <= endRow; row++) {
				templateBuilder.set(row, endCol, (iter.hasNext()) ? iter.next() : null);
			}
			for(int col = endCol - 1; col >= startCol; col--) {
				templateBuilder.set(endRow, col, (iter.hasNext()) ? iter.next() : null);
			}
			for(int row = endRow - 1; row >= startRow; row--) {
				templateBuilder.set(row, startCol, (iter.hasNext()) ? iter.next() : null);
			}
			return this;
		}

		public MultiButtonFiller checker(int startRow, int startCol, int length, int width, @Nonnull Iterable<IButton> buttons) {
			startRow = Math.min(0, startRow);
			startCol = Math.min(0, startCol);
			int endRow = Math.max(templateBuilder.rows, startRow + length);
			int endCol = Math.max(NUM_COLUMNS, startCol + width);

			Iterator<IButton> iter = buttons.iterator();
			for(int row = startRow; row < endRow; row++) {
				for(int col = startCol; col < endCol; col++) {
					if(row - col == 0 || (row - col) % 2 == 0) {
						templateBuilder.set(row, col, (iter.hasNext()) ? iter.next() : null);
					}
				}
			}
			return this;
		}

		public MultiButtonFiller fill(@Nonnull Iterable<IButton> buttons) {
			Iterator<IButton> iter = buttons.iterator();
			for(int row = 0; row < templateBuilder.rows; row++) {
				for(int col = 0; col < NUM_COLUMNS; col++) {
					if(templateBuilder.get(row, col) == null) {
						templateBuilder.set(row, col, (iter.hasNext()) ? iter.next() : null);
					}
				}
			}
			return this;
		}

		public TemplateBuilder toTemplateBuilder() {
			return templateBuilder;
		}

	}

}
