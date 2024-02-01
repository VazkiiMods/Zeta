package org.violetmoon.zeta.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.annotation.ConditionalMixin;

import java.io.IOException;
import java.util.List;

/**
 * You are on your own using this; I do not understand what's going on here with the for loops lmao
 * <p>
 * How to use:
 * call ConditionalMixinManager#shouldApply in your shouldApply of your mixin plugin and return the result of it
 */
@ApiStatus.Internal
public class ConditionalMixinManager {
    private static final Logger LOGGER = LogManager.getLogger("ZetaConditionalMixinManager");

    public static boolean shouldApply(Zeta zeta, String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(targetClassName).visibleAnnotations;
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    List<String> mods = Annotations.getValue(node, "value");
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    boolean anyModsLoaded = areModsLoaded(zeta, mods);
                    shouldApply = anyModsLoaded == applyIfPresent;
                    LOGGER.info("{}: {} is{}being applied because the mod(s) {} are{}loaded", zeta.getModDisplayName(zeta.modid), targetClassName, shouldApply ? " " : " not ", mods, anyModsLoaded ? " " : " not ");
                }
            }

            return shouldApply;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean areModsLoaded(Zeta zeta, List<String> modids) {
        for (String mod : modids) {
            if (zeta.isModLoaded(mod)) {
                return true;
            }
        }
        return false;
    }
}
