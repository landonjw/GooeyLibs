package ca.landonjw.gooeylibs2.api.button.linked;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.kyori.adventure.text.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LinkedPageButton extends GooeyButton {

    private LinkType linkType;

    protected LinkedPageButton(@Nonnull ItemStack display,
                               @Nullable Consumer<ButtonAction> onClick,
                               @Nonnull LinkType linkType) {
        super(display, onClick);
        this.linkType = linkType;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        super.onClick(action);
        if (action.getPage() instanceof LinkedPage) {
            LinkedPage linkedPage = (LinkedPage) action.getPage();
            Page targetPage = (linkType == LinkType.Previous) ? linkedPage.getPrevious() : linkedPage.getNext();
            if (targetPage != null) {
                UIManager.openUIForcefully(action.getPlayer(), targetPage);
            }
        }
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GooeyButton.Builder {

        private LinkType linkType;

        @Override
        public Builder display(@Nonnull ItemStack display) {
            super.display(display);
            return this;
        }

        @Override
        public Builder title(@Nullable ITextComponent title) {
            super.title(title);
            return this;
        }

        @Override
        public GooeyButton.Builder title(@Nullable Component title) {
            super.title(title);
            return this;
        }

        @Override
        public Builder onClick(@Nullable Consumer<ButtonAction> behaviour) {
            super.onClick(behaviour);
            return this;
        }

        public Builder linkType(@Nonnull LinkType linkType) {
            this.linkType = linkType;
            return this;
        }

        @Override
        public LinkedPageButton build() {
            validate();
            return new LinkedPageButton(buildDisplay(), onClick, linkType);
        }

        @Override
        protected void validate() {
            super.validate();
            if (linkType == null) {
                throw new IllegalStateException("link type must be defined!");
            }
        }
    }

}
