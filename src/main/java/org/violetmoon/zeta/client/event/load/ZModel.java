package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.Map;

public interface ZModel extends IZetaLoadEvent {
    interface ModifyBakingResult extends ZModel {
        Map<ModelResourceLocation, BakedModel> getModels();
        ModelBakery getModelBakery();
    }

    interface BakingCompleted extends ZModel {
        ModelManager getModelManager();
        Map<ModelResourceLocation, BakedModel> getModels();
        ModelBakery getModelBakery();
    }

    interface RegisterAdditional extends ZModel {
        void register(ModelResourceLocation model);
    }

    interface RegisterGeometryLoaders extends ZModel {
        void register(ResourceLocation id, IGeometryLoader<?> loader);
    }
}
