package org.violetmoon.zeta.recipe;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

// Copy of Forge IConditionSerializer
public interface IZetaConditionSerializer<T extends IZetaCondition> {
	void write(JsonObject json, T value);

	T read(JsonObject json);

	ResourceLocation getID();

	default JsonObject getJson(T value)
	{
		JsonObject json = new JsonObject();
		this.write(json, value);
		json.addProperty("type", value.getID().toString());
		return json;
	}
}
