package org.violetmoon.zeta.client.event.load;

import java.util.Map;

import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

public interface ZModel extends IZetaLoadEvent {
    interface ModifyBakingResult extends ZModel {
        Map<ResourceLocation, BakedModel> getModels();
        ModelBakery getModelBakery();
    }

    interface BakingCompleted extends ZModel {
        ModelManager getModelManager();
        Map<ResourceLocation, BakedModel> getModels();
        ModelBakery getModelBakery();
    }

    interface RegisterAdditional extends ZModel {
        void register(ResourceLocation model);
    }

    interface RegisterGeometryLoaders extends ZModel {
        //fixme
        //void register(String name, IGeometryLoader<?> loader);
    }
}
