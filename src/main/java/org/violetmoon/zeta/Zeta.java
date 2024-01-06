package org.violetmoon.zeta;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Zeta.MODID)
public class Zeta {
    public static final String MODID = "zeta";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public Zeta() {
        LOGGER.info("Look mom im a minecraft mod!");
    }
}
