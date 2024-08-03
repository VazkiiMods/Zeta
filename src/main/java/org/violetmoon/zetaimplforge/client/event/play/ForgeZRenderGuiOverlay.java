package org.violetmoon.zetaimplforge.client.event.play;

import net.minecraftforge.eventbus.api.Event;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class ForgeZRenderGuiOverlay  implements ZRenderGuiOverlay {
	public final RenderGuiOverlayEvent e;

	public ForgeZRenderGuiOverlay(RenderGuiOverlayEvent e) {
		this.e = e;
	}

	@Override
	public Window getWindow() {
		return e.getWindow();
	}

	@Override
	public GuiGraphics getGuiGraphics() {
		return e.getGuiGraphics();
	}

	@Override
	public float getPartialTick() {
		return e.getPartialTick();
	}

	@Override
	public boolean shouldDrawSurvivalElements() {
		return Minecraft.getInstance().gui instanceof ForgeGui fgui && fgui.shouldDrawSurvivalElements();
	}

	@Override
	public int getLeftHeight() {
		return Minecraft.getInstance().gui instanceof ForgeGui fgui ? fgui.leftHeight : 39;
	}

	public static class Hotbar extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Hotbar {
		public Hotbar(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.Hotbar implements ZRenderGuiOverlay.Hotbar.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.Hotbar implements ZRenderGuiOverlay.Hotbar.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class Crosshair extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Crosshair {
		public Crosshair(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.Crosshair implements ZRenderGuiOverlay.Crosshair.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.Crosshair implements ZRenderGuiOverlay.Crosshair.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class PlayerHealth extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.PlayerHealth {
		public PlayerHealth(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.PlayerHealth implements ZRenderGuiOverlay.PlayerHealth.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.PlayerHealth implements ZRenderGuiOverlay.PlayerHealth.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class ArmorLevel extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.ArmorLevel {
		public ArmorLevel(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.ArmorLevel implements ZRenderGuiOverlay.ArmorLevel.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.ArmorLevel implements ZRenderGuiOverlay.ArmorLevel.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class DebugText extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.DebugText {
		public DebugText(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.DebugText implements ZRenderGuiOverlay.DebugText.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.DebugText implements ZRenderGuiOverlay.DebugText.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class PotionIcons extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.PotionIcons {
		public PotionIcons(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.PotionIcons implements ZRenderGuiOverlay.PotionIcons.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.PotionIcons implements ZRenderGuiOverlay.PotionIcons.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class ChatPanel extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.ChatPanel {
		public ChatPanel(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.ChatPanel implements ZRenderGuiOverlay.ChatPanel.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.ChatPanel implements ZRenderGuiOverlay.ChatPanel.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}
}
