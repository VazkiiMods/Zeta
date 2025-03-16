package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.event.load.ZAddBlockColorHandlers;
import org.violetmoon.zeta.event.load.ZGatherHints;
import org.violetmoon.zeta.registry.ZetaRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ForgeZAddBlockColorHandlers implements ZAddBlockColorHandlers {
    public final RegisterColorHandlersEvent.Block e;

    public ForgeZAddBlockColorHandlers(RegisterColorHandlersEvent.Block e) {
        this.e = e;
    }

    @Override
    public void register(BlockColor blockColor, Block... blocks) {
        e.register(blockColor, blocks);
    }

    // yes passing zeta like this here is terribly ugly but i cant add more params to this event since it's a forge event wrapper
    @Override
    public void registerNamed(Zeta myZeta, Function<Block, BlockColor> c, String... names) {
        for (String name : names) {
            //why the need for an event? cant we just call this directly?
            myZeta.registry.assignBlockColor(name, b -> register(c.apply(b), b));
        }
    }

    @Override
    public BlockColors getBlockColors() {
        return e.getBlockColors();
    }

}
