package org.violetmoon.zeta.event.play;

import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public interface ZPlayNoteBlock extends IZetaPlayEvent, Cancellable {
	LevelAccessor getLevel();
	BlockPos getPos();
	BlockState getState();

	int getVanillaNoteId();
	NoteBlockInstrument getInstrument();
}
