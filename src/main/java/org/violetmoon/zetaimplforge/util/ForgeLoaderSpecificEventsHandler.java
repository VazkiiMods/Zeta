package org.violetmoon.zetaimplforge.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.violetmoon.zeta.util.handler.LoaderSpecificEventsHandler;

public class ForgeLoaderSpecificEventsHandler implements LoaderSpecificEventsHandler {

    @Override
    public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
        return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
    }
}
