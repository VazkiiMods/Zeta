package org.violetmoon.zetaimplforge.client.event.play;

import java.util.List;

import org.violetmoon.zeta.client.event.play.ZRenderTooltip;

import com.mojang.datafixers.util.Either;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class ForgeZRenderTooltip implements ZRenderTooltip {
    public static class GatherComponents extends ForgeZRenderTooltip implements ZRenderTooltip.GatherComponents {
        private final RenderTooltipEvent.GatherComponents e;

        public GatherComponents(RenderTooltipEvent.GatherComponents e) {
            this.e = e;
        }

        @Override
        public ItemStack getItemStack() {
            return e.getItemStack();
        }

        @Override
        public List<Either<FormattedText, TooltipComponent>> getTooltipElements() {
            return e.getTooltipElements();
        }

        public static class Low extends ForgeZRenderTooltip.GatherComponents implements ZRenderTooltip.GatherComponents.Low {
            public Low(RenderTooltipEvent.GatherComponents e) {
                super(e);
            }
        }
    }
}
