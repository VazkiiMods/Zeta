package org.violetmoon.zeta.config;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;
import java.util.stream.Collectors;

public class SyncedFlagHandler {
	private static ConfigFlagManager flagManager;
	private static List<String> sortedFlags;

	public static void setupFlagManager(ConfigFlagManager manager) {
		if (manager != null) {
			flagManager = manager;

			//specifying the type of collection explicitly, since a hashCode is done over it and I don't want surprises
			sortedFlags = manager.getAllFlags().stream().sorted().collect(Collectors.toCollection(ArrayList::new));
		}
	}

	public static BitSet compileFlagInfo() {
		BitSet set = new BitSet();
		int i = 0;
		for(String flag : sortedFlags)
			set.set(i++, flagManager.getFlag(flag));

		return set;
	}

	public static int expectedLength() {
		return sortedFlags.size();
	}

	public static int expectedHash() {
		return sortedFlags.hashCode();
	}

	private static Set<String> decodeFlags(BitSet bitSet) {
		Set<String> enabledFlags = new HashSet<>();

		for(int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			enabledFlags.add(sortedFlags.get(i));
		}

		return enabledFlags;
	}

	public static void receiveFlagInfoFromPlayer(ServerPlayer player, BitSet bitSet) {
		flagsFromPlayers.put(player, decodeFlags(bitSet));
	}

	@OnlyIn(Dist.CLIENT)
	public static void receiveFlagInfoFromServer(BitSet bitSet) {
		flagsFromServer.put(Minecraft.getInstance().getConnection(), decodeFlags(bitSet));
	}

	private static final WeakHashMap<PacketListener, Set<String>> flagsFromServer = new WeakHashMap<>();
	private static final WeakHashMap<ServerPlayer, Set<String>> flagsFromPlayers = new WeakHashMap<>();

	public static boolean getFlagForPlayer(ServerPlayer player, String flag) {
		Set<String> enabledFlags = flagsFromPlayers.get(player);
		if(enabledFlags == null)
			return flagManager.getFlag(flag);

		return enabledFlags.contains(flag);
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean getFlagForServer(String flag) {
		for(PacketListener listener : flagsFromServer.keySet()) {
			Set<String> enabledFlags = flagsFromServer.get(listener);
			if(enabledFlags != null)
				return enabledFlags.contains(flag);
		}

		return flagManager.getFlag(flag);
	}
}
