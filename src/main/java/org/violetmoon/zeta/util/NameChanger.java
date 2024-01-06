package org.violetmoon.zeta.util;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class NameChanger {

	/**
	 * Submit or rescind (depending on `enabled`) a request to change the translation key of a block.
	 * <p>
	 * The most recently submitted name change request will win. If there are no outstanding requests,
	 * the block will return to its original vanilla name.
	 */
	public void changeBlock(Block toChange, String newTranslationKey, boolean enabled) {
		changeBlockStatic(toChange, newTranslationKey, enabled);
	}

	public void changeItem(Item toChange, String newTranslationKey, boolean enabled) {
		changeItemStatic(toChange, newTranslationKey, enabled);
	}

	// this stuff is implemented statically so that originalBlockNames really does contain the *original* block names.
	// if this wasn't static, you could imagine one Zeta mod changes the name of a block, then another mod changes
	// the name of the same block; it'd end up thinking the first mod's *changed* name is the *original* name.

	// Most of the complexity of this class is about handling cases like 'mod A changes block name, mod B changes
	// the same block name, mod A removes its name-change" -> we should select mod B's name.

	protected static Map<Block, String> originalBlockNames = new IdentityHashMap<>();
	protected static Map<Block, NameChangeRequests> changedBlockNames = new IdentityHashMap<>();
	protected static Map<Item, String> originalItemNames = new IdentityHashMap<>();
	protected static Map<Item, NameChangeRequests> changedItemNames = new IdentityHashMap<>();

	// marked "synchronized" cause uhhhh ?? forge??... idk... might be a good idea
	protected static synchronized void changeBlockStatic(Block toChange, String newTranslationKey, boolean enabled) {
		//keep track of the original name for this block
		originalBlockNames.computeIfAbsent(toChange, Block::getDescriptionId);

		//add the changed name onto the pile
		NameChangeRequests changeRequests = changedBlockNames.computeIfAbsent(toChange, __ -> new NameChangeRequests());
		if(enabled)
			changeRequests.add(newTranslationKey);
		else
			changeRequests.remove(newTranslationKey);

		//actually change the block's name - if there are any outstanding name-change requests, use the most recent one,
		//else use the block's original name
		toChange.descriptionId = changeRequests.lastOrElse(originalBlockNames.get(toChange));

		//save a tiny bit of memory
		if(changeRequests.isEmpty())
			changedBlockNames.remove(toChange);
	}

	protected static synchronized void changeItemStatic(Item toChange, String newTranslationKey, boolean enabled) {
		originalItemNames.computeIfAbsent(toChange, Item::getDescriptionId);

		NameChangeRequests changeRequests = changedItemNames.computeIfAbsent(toChange, __ -> new NameChangeRequests());
		if(enabled)
			changeRequests.add(newTranslationKey);
		else
			changeRequests.remove(newTranslationKey);
		toChange.descriptionId = changeRequests.lastOrElse(originalItemNames.get(toChange));

		if(changeRequests.isEmpty())
			changedItemNames.remove(toChange);
	}

	// In practice these collections will contain like, 1 element at-most, *maybe* 2, so the O(n) algorithm choice is intentional
	// Bro i,m starting to think i overengineered this class :skull:
	protected static class NameChangeRequests {
		List<String> list = new ArrayList<>(1);

		public void add(String value) {
			// move it to the end, so the most recent requests get prioritized
			remove(value);
			list.add(value);
		}

		public void remove(String value) {
			list.remove(value);
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}

		public String lastOrElse(String orElse) {
			if(list.isEmpty())
				return orElse;
			else
				return list.get(list.size() - 1);
		}
	}
}
