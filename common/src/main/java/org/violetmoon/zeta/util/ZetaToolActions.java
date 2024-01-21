package org.violetmoon.zeta.util;

public class ZetaToolActions {
    /**
     For any tool upon digging, replaces forge's separate digging fields.
    */
    public static final ZetaToolAction TOOL_DIG = new ZetaToolAction("tool_dig");
    public static final ZetaToolAction AXE_STRIP = new ZetaToolAction("axe_strip");
    public static final ZetaToolAction AXE_SCRAPE = new ZetaToolAction("axe_scrape");
    public static final ZetaToolAction AXE_WAX_OFF = new ZetaToolAction("axe_wax_off");
    public static final ZetaToolAction SHOVEL_FLATTEN = new ZetaToolAction("shovel_flatten");
    public static final ZetaToolAction SHEARS_HARVEST = new ZetaToolAction("shears_harvest");
    public static final ZetaToolAction SHEARS_CARVE = new ZetaToolAction("shears_carve");
    public static final ZetaToolAction SHEARS_DISARM = new ZetaToolAction("shears_disarm");
    public static final ZetaToolAction HOE_TILL = new ZetaToolAction("hoe_till");
    public static final ZetaToolAction SHIELD_BLOCK = new ZetaToolAction("shield_block");
    public static final ZetaToolAction CAST_ROD = new ZetaToolAction("cast_rod");





    public record ZetaToolAction(String name) {}
}
