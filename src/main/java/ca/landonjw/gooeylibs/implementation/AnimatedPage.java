package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.GooeyButton;
import ca.landonjw.gooeylibs.api.data.UpdateEmitter;
import ca.landonjw.gooeylibs.api.page.Page;
import ca.landonjw.gooeylibs.api.template.Template;
import ca.landonjw.gooeylibs.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AnimatedPage extends UpdateEmitter<Page> implements Page {

    private final Template template;
    private final List<Integer> animationIndexes = Lists.newArrayList();
    private int frameIndex;

    private final GooeyButton filler = GooeyButton.builder()
            .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GRAY.getMetadata()))
            .build();

    private final GooeyButton diamond = GooeyButton.builder()
            .display(new ItemStack(Items.DIAMOND))
            .build();

    public AnimatedPage() {
        this.template = ChestTemplate.builder(6)
                .fill(filler)
                .build();

        fillAnimationIndexes();
        startAnimation();
    }

    private void fillAnimationIndexes() {
        for (int i = 0; i < 9; i++) animationIndexes.add(i);
        for (int i = 17; i < 54; i += 9) animationIndexes.add(i);
        for (int i = 52; i > 44; i--) animationIndexes.add(i);
        for (int i = 45; i > 0; i -= 9) animationIndexes.add(i);
    }

    private void startAnimation() {
        Task.builder()
                .execute(() -> {
                    getTemplate().getSlot(animationIndexes.get(frameIndex)).setButton(filler);
                    frameIndex = ++frameIndex % animationIndexes.size();
                    getTemplate().getSlot(animationIndexes.get(frameIndex)).setButton(diamond);
                })
                .infinite()
                .interval(20)
                .build();
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return "Animated Page";
    }

}
