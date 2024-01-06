package org.violetmoon.zeta.util;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BooleanSuppliers {
	private BooleanSuppliers() {}

	public static final BooleanSupplier TRUE = () -> true;
	public static final BooleanSupplier FALSE = () -> false;

	public static BooleanSupplier and(BooleanSupplier a, BooleanSupplier b) {
		if(a == FALSE || b == FALSE)
			return FALSE;
		if(a == TRUE)
			return b;
		if(b == TRUE)
			return a;

		return () -> a.getAsBoolean() && b.getAsBoolean();
	}

	public static BooleanSupplier or(BooleanSupplier a, BooleanSupplier b) {
		if(a == TRUE || b == TRUE)
			return TRUE;
		if(a == FALSE)
			return b;
		if(b == FALSE)
			return a;

		return () -> a.getAsBoolean() || b.getAsBoolean();
	}

	public static BooleanSupplier not(BooleanSupplier x) {
		if(x == TRUE)
			return FALSE;
		if(x == FALSE)
			return TRUE;

		return () -> !x.getAsBoolean();
	}

	private static final Supplier<Boolean> BOXED_TRUE = () -> true;
	private static final Supplier<Boolean> BOXED_FALSE = () -> false;

	public static Supplier<Boolean> boxed(BooleanSupplier x) {
		if(x == TRUE)
			return BOXED_TRUE;
		if(x == FALSE)
			return BOXED_FALSE;

		return x::getAsBoolean;
	}
}
