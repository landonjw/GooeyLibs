package ca.landonjw.gooeylibs.implementation;

import ca.landonjw.gooeylibs.api.button.Button;
import ca.landonjw.gooeylibs.api.data.EventEmitterBase;
import ca.landonjw.gooeylibs.api.page.IPage;
import ca.landonjw.gooeylibs.api.template.ITemplate;
import ca.landonjw.gooeylibs.api.template.chest.ChestTemplate;
import ca.landonjw.gooeylibs.implementation.tasks.Task;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AnimatedPage extends EventEmitterBase<IPage> implements IPage {

    private final ChestTemplate template;

    private final List<Integer> animationIndexes = Lists.newArrayList();
    private int frameIndex;

    private final Button filler = Button.builder()
            .display(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GRAY.getMetadata()))
            .build();

    private final Button diamond = Button.builder()
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
                    template.getSlot(animationIndexes.get(frameIndex)).setButton(filler);
                    frameIndex = ++frameIndex % animationIndexes.size();
                    template.getSlot(animationIndexes.get(frameIndex)).setButton(diamond);
                })
                .infinite()
                .interval(5)
                .build();
    }

    @Override
    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public String getTitle() {
        return "Animated Page";
    }

}
