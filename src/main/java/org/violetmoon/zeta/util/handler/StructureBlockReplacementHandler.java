package org.violetmoon.zeta.util.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class StructureBlockReplacementHandler {

	private static Set<StructureFunction> functions = new HashSet<>();

	private static final ThreadLocal<StructureHolder> structureHolder = new ThreadLocal<>();

	public static void addReplacement(StructureFunction func) {
		functions.add(func);
	}

	@Nullable
	public static Holder<Structure> getStructure(ServerLevelAccessor accessor, StructureHolder structure) {
		Optional<? extends Registry<Structure>> registry = accessor.registryAccess().registry(Registries.STRUCTURE);
		Optional<Holder<Structure>> holder = registry
				.flatMap((reg) -> reg.getResourceKey(structure.currentStructure).flatMap(reg::getHolder));

		return holder.isEmpty() ? null : holder.get();
	}

	@Nullable
	public static ResourceKey<Structure> getStructureKey(ServerLevelAccessor accessor, StructureHolder structure) {
		Optional<ResourceKey<Structure>> res = accessor.registryAccess().registry(Registries.STRUCTURE)
				.flatMap((it) -> it.getResourceKey(structure.currentStructure));

		return res.isEmpty() ? null : res.get();
	}

	@Nullable
	public static ResourceLocation getStructureRes(ServerLevelAccessor accessor, StructureHolder structure) {
		Optional<ResourceLocation> res = accessor.registryAccess().registry(Registries.STRUCTURE)
				.map((it) -> it.getKey(structure.currentStructure));

		return res.isEmpty() ? null : res.get();
	}

	public static boolean isStructure(ServerLevelAccessor accessor, StructureHolder structure, ResourceKey<Structure> target) {
		ResourceKey<Structure> curr = getStructureKey(accessor, structure);
		return curr != null && curr.equals(target);
	}

	public static BlockState getResultingBlockState(ServerLevelAccessor level, BlockState blockstate) {
		StructureHolder curr = getCurrentStructureHolder();

		if(curr != null && curr.currentStructure != null)
			for(StructureFunction fun : functions) {

				BlockState res = fun.transformBlockstate(level, blockstate, curr);
				if(res != null)
					return res;
			}

		return blockstate;
	}

	private static StructureHolder getCurrentStructureHolder() {
		return structureHolder.get();
	}

	public static void setActiveStructure(Structure structure, PiecesContainer components) {
		StructureHolder curr = getCurrentStructureHolder();
		if(curr == null) {
			curr = new StructureHolder();
			structureHolder.set(curr);
		}

		curr.currentStructure = structure;
		curr.currentComponents = components == null ? null : components.pieces();
	}

	@FunctionalInterface
	public interface StructureFunction {
		BlockState transformBlockstate(ServerLevelAccessor level, BlockState state, StructureHolder structureHolder);
	}

	public static class StructureHolder {
		public Structure currentStructure;
		public List<StructurePiece> currentComponents;
	}

}
