package org.violetmoon.zetaimplforge.util;

import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.ZetaEntityTargetType;
import org.violetmoon.zeta.util.ZetaToolActions;

public class ConversionUtil {
    public static ZetaToolActions.ZetaToolAction forgeToZetaToolActions(ToolAction toolAction) {
        String name = toolAction.name();
        return switch (name) {
            case "axe_dig", "shovel_dig", "pickaxe_dig", "sword_dig", "hoe_dig", "shears_dig" -> ZetaToolActions.TOOL_DIG;
            case "axe_strip" -> ZetaToolActions.AXE_STRIP;
            case "axe_scrape" -> ZetaToolActions.AXE_SCRAPE;
            case "axe_wax_off" -> ZetaToolActions.AXE_WAX_OFF;
            case "shovel_flatten" -> ZetaToolActions.SHOVEL_FLATTEN;
            case "shears_harvest" -> ZetaToolActions.SHEARS_HARVEST;
            case "shears_carve" -> ZetaToolActions.SHEARS_CARVE;
            case "shears_disarm" -> ZetaToolActions.SHEARS_DISARM;
            case "hoe_till" -> ZetaToolActions.HOE_TILL;
            case "shield_block" -> ZetaToolActions.SHIELD_BLOCK;
            case "cast_rod" -> ZetaToolActions.CAST_ROD;

            default -> {
                Zeta.GLOBAL_LOG.error("ToolAction conversion failed, replacing with new tool action.");
                yield new ZetaToolActions.ZetaToolAction(name);
            }
        };
    }
    
    public static ToolAction zetaToForgeToolAction(ZetaToolActions.ZetaToolAction toolAction) {
        String name = toolAction.name();
        return switch (name) {
            case "tool_dig" -> ToolActions.PICKAXE_DIG; // All are converted to PICKAXE_DIG, if thats a problem just rely on what forge provides.
            case "axe_strip" -> ToolActions.AXE_STRIP;
            case "axe_scrape" -> ToolActions.AXE_SCRAPE;
            case "axe_wax_off" -> ToolActions.AXE_WAX_OFF;
            case "shovel_flatten" -> ToolActions.SHOVEL_FLATTEN;
            case "shears_harvest" -> ToolActions.SHEARS_HARVEST;
            case "shears_carve" -> ToolActions.SHEARS_CARVE;
            case "shears_disarm" -> ToolActions.SHEARS_DISARM;
            case "hoe_till" -> ToolActions.HOE_TILL;
            case "shield_block" -> ToolActions.SHIELD_BLOCK;
            case "cast_rod" -> ToolActions.FISHING_ROD_CAST;

            default -> {
                Zeta.GLOBAL_LOG.error("ToolAction conversion failed, replacing with new tool action.");
                yield ToolAction.get(name);
            }
        };
    }

    public static ZetaEntityTargetType forgeToZetaTargetChange(LivingChangeTargetEvent.ILivingTargetType targetType) {
        if (targetType == LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET) {
            return ZetaEntityTargetType.BEHAVIOR_TARGET;
        } else if (targetType == LivingChangeTargetEvent.LivingTargetType.MOB_TARGET) {
            return ZetaEntityTargetType.MOB_TARGET;
        } else return ZetaEntityTargetType.MISC_TARGET;
    }
}
