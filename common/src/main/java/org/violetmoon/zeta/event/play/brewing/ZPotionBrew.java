package org.violetmoon.zeta.event.play.brewing;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZPotionBrew extends IZetaPlayEvent {
    NonNullList<ItemStack> getStacks();

    @NotNull ItemStack getItem(int index);

    void setItem(int index, @NotNull ItemStack stack);

    int getLength();

    /**
     * ZPotionBrew.Pre is fired before vanilla brewing takes place.
     * All changes made to the event's array will be made to the TileEntity if the event is canceled.
     * <br>
     * The event is fired during the {@code BrewingStandBlockEntity#doBrew(Level, BlockPos, NonNullList)} method invocation.<br>
     * <br>
     * {@link #getStacks()} contains the itemstack array from the TileEntityBrewer holding all items in Brewer.<br>
     * <br>
     * This event is {@link org.violetmoon.zeta.event.bus.Cancellable}.<br>
     * If the event is not canceled, the vanilla brewing will take place instead of modded brewing.
     * <br>
     * This event does not have a result.<br>
     * <br>
     * If this event is canceled, and items have been modified, ZPotionBrew.Post will automatically be fired.
     **/
    interface Pre extends ZPotionBrew { }

    /**
     * ZPotionBrew.Post is fired when a potion is brewed in the brewing stand.
     * <br>
     * The event is fired during the {@code BrewingStandBlockEntity#doBrew(Level, BlockPos, NonNullList)} method invocation.<br>
     * <br>
     * {@link #getStacks} contains the itemstack array from the TileEntityBrewer holding all items in Brewer.<br>
     * <br>
     * This event is not {@link org.violetmoon.zeta.event.bus.Cancellable}.<br>
     * <br>
     * This event does not have a result.<br>
     **/
    interface Post extends ZPotionBrew { }
}
