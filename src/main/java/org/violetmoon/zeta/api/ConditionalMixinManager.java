package org.violetmoon.zeta.api;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.annotation.ConditionalMixin;
import org.violetmoon.zeta.annotation.Requirement;

import java.io.IOException;
import java.util.List;

/**
 * You are on your own using this; I do not understand what's going on here with the for loops lmao
 */
@ApiStatus.Internal
public class ConditionalMixinManager {
    public static boolean shouldApply(Zeta zeta, String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(targetClassName).visibleAnnotations;
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    List<Requirement> requirements = Annotations.getValue(node, "require", Requirement.class);
                    for (Requirement req : requirements) {
                        String[] modids = req.value();

                        shouldApply = areModsLoaded(zeta, modids);

                        Zeta.GLOBAL_LOG.info("{}: {} is{}being applied because the mod(s) {} are{}loaded", zeta.getModDisplayName(zeta.modid), targetClassName, shouldApply ? " " : " not ", modids, shouldApply ? " " : " not ");
                    }

                    List<Requirement> conflicts = Annotations.getValue(node, "conflict", Requirement.class);
                    for (Requirement conflict : conflicts) {
                        String[] modids = conflict.value();

                        shouldApply = areModsLoaded(zeta, modids);

                        Zeta.GLOBAL_LOG.info("{}: {} is{}being applied because the mod(s) {} are{}loaded", zeta.getModDisplayName(zeta.modid), targetClassName, shouldApply ? " " : " not ", modids, shouldApply ? " " : " not ");
                    }
                }
            }

            return shouldApply;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean areModsLoaded(Zeta zeta, String[] modids) {
        for (String mod : modids) {
            if (zeta.isModLoaded(mod)) {
                return true;
            }
        }
        return false;
    }
}
