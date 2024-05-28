/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the ThaumicTinkerer Mod.
 *
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4.
 * Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 *
 * File Created @ [8 Sep 2013, 19:36:25 (GMT)]
 */
package org.violetmoon.zeta.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

// TBH this entire class could be Deprecated and removed
public final class ItemNBTHelper {

	/** Checks if an ItemStack has a Tag Compound **/
	@Deprecated(forRemoval = true) // Just use stack.hasTag()
	public static boolean detectNBT(ItemStack stack) {
		return stack.hasTag();
	}

	/** Tries to initialize an NBT Tag Compound in an ItemStack,
	 * this will not do anything if the stack already has a tag
	 * compound **/
	@Deprecated(forRemoval = true) // Just use stack.getOrCreateTag()
	public static void initNBT(ItemStack stack) {
		stack.getOrCreateTag();
	}

	/** Injects an NBT Tag Compound to an ItemStack, no checks
	 * are made previously **/
	@Deprecated(forRemoval = true) // Just use stack.setTag(nbt)
	public static void injectNBT(ItemStack stack, CompoundTag nbt) {
		stack.setTag(nbt);
	}

	/** Gets the CompoundNBT in an ItemStack. Tries to init it
	 * previously in case there isn't one present **/
	@Deprecated(forRemoval = true) // Just use stack.getOrCreateTag()
	public static CompoundTag getNBT(ItemStack stack) {
		return stack.getOrCreateTag();
	}

	// SETTERS ///////////////////////////////////////////////////////////////////

	// All these force create a tag if it doesn't exist

	public static void setBoolean(ItemStack stack, String tag, boolean b) {
		stack.getOrCreateTag().putBoolean(tag, b);
	}

	public static void setByte(ItemStack stack, String tag, byte b) {
		stack.getOrCreateTag().putByte(tag, b);
	}

	public static void setShort(ItemStack stack, String tag, short s) {
		stack.getOrCreateTag().putShort(tag, s);
	}

	public static void setInt(ItemStack stack, String tag, int i) {
		stack.getOrCreateTag().putInt(tag, i);
	}

	public static void setLong(ItemStack stack, String tag, long l) {
		stack.getOrCreateTag().putLong(tag, l);
	}

	public static void setFloat(ItemStack stack, String tag, float f) {
		stack.getOrCreateTag().putFloat(tag, f);
	}

	public static void setDouble(ItemStack stack, String tag, double d) {
		stack.getOrCreateTag().putDouble(tag, d);
	}

	public static void setCompound(ItemStack stack, String tag, CompoundTag cmp) {
		if(!tag.equalsIgnoreCase("ench")) // not override the enchantments
			stack.getOrCreateTag().put(tag, cmp);
	}

	public static void setString(ItemStack stack, String tag, String s) {
		stack.getOrCreateTag().putString(tag, s);
	}

	public static void setList(ItemStack stack, String tag, ListTag list) {
		stack.getOrCreateTag().put(tag, list);
	}

	// GETTERS ///////////////////////////////////////////////////////////////////


	public static boolean verifyExistence(ItemStack stack, String tag) {
		return !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().contains(tag);
	}

	@Deprecated(forRemoval = true)
	public static boolean verifyExistance(ItemStack stack, String tag) {
		return verifyExistence(stack, tag);
	}

	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getBoolean(tag) : defaultExpected;
	}

	public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getByte(tag) : defaultExpected;
	}

	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getShort(tag) : defaultExpected;
	}

	public static int getInt(ItemStack stack, String tag, int defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getInt(tag) : defaultExpected;
	}

	public static long getLong(ItemStack stack, String tag, long defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getLong(tag) : defaultExpected;
	}

	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getFloat(tag) : defaultExpected;
	}

	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getDouble(tag) : defaultExpected;
	}

	/** If nullifyOnFail is true it'll return null if it doesn't find any
	 * compounds, otherwise it'll return a new one. **/
	public static CompoundTag getCompound(ItemStack stack, String tag, boolean nullifyOnFail) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getCompound(tag) : nullifyOnFail ? null : new CompoundTag();
	}

	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getString(tag) : defaultExpected;
	}

	public static ListTag getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
		return verifyExistence(stack, tag) ? stack.getOrCreateTag().getList(tag, objtype) : nullifyOnFail ? null : new ListTag();
	}

}
