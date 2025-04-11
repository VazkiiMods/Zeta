package org.violetmoon.zetaimplforge.event.play;

import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.Event;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.event.play.ZRecipeCrawl;

public class ForgeZRecipeCrawl<W extends ZRecipeCrawl> extends Event implements IZetaPlayEvent {

    private final W wrapped;

    public ForgeZRecipeCrawl(W wrapped) {
        this.wrapped = wrapped;
    }

    public W get() {
        return wrapped;
    }

    public static class Reset extends ForgeZRecipeCrawl<ZRecipeCrawl.Reset> {
        public Reset(ZRecipeCrawl.Reset wrapped) {
            super(wrapped);
        }
    }

    public static class Starting extends ForgeZRecipeCrawl<ZRecipeCrawl.Starting> {
        public Starting(ZRecipeCrawl.Starting wrapped) {
            super(wrapped);
        }
    }

    public static class Digest extends ForgeZRecipeCrawl<ZRecipeCrawl.Digest> {
        public Digest(ZRecipeCrawl.Digest wrapped) {
            super(wrapped);
        }
    }

    public static class Visit<T extends Recipe<?>> extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit> {
        public Visit(ZRecipeCrawl.Visit wrapped) {
            super(wrapped);
        }

        public static class Shaped extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit.Shaped> {
            public Shaped(ZRecipeCrawl.Visit.Shaped wrapped) {
                super(wrapped);
            }
        }

        public static class Shapeless extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit.Shapeless> {
            public Shapeless(ZRecipeCrawl.Visit.Shapeless wrapped) {
                super(wrapped);
            }
        }

        public static class Custom extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit.Custom> {
            public Custom(ZRecipeCrawl.Visit.Custom wrapped) {
                super(wrapped);
            }
        }

        public static class Cooking extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit.Cooking> {
            public Cooking(ZRecipeCrawl.Visit.Cooking wrapped) {
                super(wrapped);
            }
        }

        public static class Misc extends ForgeZRecipeCrawl<ZRecipeCrawl.Visit.Misc> {
            public Misc(ZRecipeCrawl.Visit.Misc wrapped) {
                super(wrapped);
            }
        }

    }






}
