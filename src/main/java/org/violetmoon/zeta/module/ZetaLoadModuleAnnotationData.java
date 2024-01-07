package org.violetmoon.zeta.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exists mainly because Forge ModFileScanData doesn't give you the annotation itself :S
 *
 * @see org.violetmoon.zeta.module.ZetaLoadModule
 */
public record ZetaLoadModuleAnnotationData(
	Class<?> clazz,

	//and the rest is from ZetaLoadModule
	String category,
	String name,
	String description,
	String[] antiOverlap,
	boolean enabledByDefault,
	boolean clientReplacement,
	int loadPhase
) {
	public static ZetaLoadModuleAnnotationData fromAnnotation(Class<?> clazz, ZetaLoadModule annotation) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			annotation.category(),
			annotation.name(),
			annotation.description(),
			annotation.antiOverlap(),
			annotation.enabledByDefault(),
			annotation.clientReplacement(),
			annotation.loadPhase()
		);
	}

	//clunky
	@SuppressWarnings("unchecked")
	public static ZetaLoadModuleAnnotationData fromForgeThing(Class<?> clazz, Map<String, Object> data) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			(String) data.get("category"),
			(String) data.getOrDefault("name", ""),
			(String) data.getOrDefault("description", ""),
			((List<String>) data.getOrDefault("antiOverlap", new ArrayList<String>())).toArray(new String[0]),
			(boolean) data.getOrDefault("enabledByDefault", true),
			(boolean) data.getOrDefault("clientReplacement", false),
			(int) data.getOrDefault("loadPhase", 0)
		);
	}
}
