package org.violetmoon.zeta.client.config.definition;

import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.config.SectionDefinition;

public interface IConfigDefinitionProvider {

	@NotNull ClientDefinitionExt<SectionDefinition> getClientConfigDefinition(SectionDefinition parent);
	
}
