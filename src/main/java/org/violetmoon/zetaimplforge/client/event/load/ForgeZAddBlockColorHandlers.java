package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.registry.ZetaRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgeZAddBlockColorHandlers implements ZAddBlockColorHandlers {
    protected final RegisterColorHandlersEvent.Block e;
    private final ZetaRegistry zetaRegistry;

    public ForgeZAddBlockColorHandlers(RegisterColorHandlersEvent.Block e, ZetaRegistry zetaRegistry) {
        this.e = e;
        this.zetaRegistry = zetaRegistry;
    }

    @Override
    public void register(BlockColor blockColor, Block... blocks) {
        e.register(blockColor, blocks);
    }

    @Override
    public void registerNamed(Function<Block, BlockColor> c, String... names) {
        for (String name : names) {
            zetaRegistry.assignBlockColor(name, b -> register(c.apply(b), b));
        }
    }

    @Override
    public BlockColors getBlockColors() {
        return e.getBlockColors();
    }

}
