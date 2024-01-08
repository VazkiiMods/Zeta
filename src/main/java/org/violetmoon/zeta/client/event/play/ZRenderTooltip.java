package org.violetmoon.zeta.client.event.play;

import java.util.List;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import com.mojang.datafixers.util.Either;

import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public interface ZRenderTooltip extends IZetaPlayEvent {
    interface GatherComponents extends IZetaPlayEvent, ZRenderTooltip {
        ItemStack getItemStack();
        List<Either<FormattedText, TooltipComponent>> getTooltipElements();

        interface Low extends GatherComponents { }
    }
}
