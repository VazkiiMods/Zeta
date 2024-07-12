package org.violetmoon.zetaimplforge.client.event.play;

import net.minecraft.client.DeltaTracker;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.violetmoon.zeta.client.event.play.ZRenderGuiOverlay;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ForgeZRenderGuiOverlay implements ZRenderGuiOverlay {
	private final RenderGuiLayerEvent e;

	public ForgeZRenderGuiOverlay(RenderGuiLayerEvent e) {
		this.e = e;
	}

	@Override
	public Window getWindow() {
		return Minecraft.getInstance().getWindow();
	}

	@Override
	public GuiGraphics getGuiGraphics() {
		return e.getGuiGraphics();
	}

	@Override
	public DeltaTracker getPartialTick() {
		return e.getPartialTick();
	}

	@Override
	public boolean shouldDrawSurvivalElements() {
		return Minecraft.getInstance().gameMode.canHurtPlayer();
	}

	@Override
	public int getLeftHeight() {
		return Minecraft.getInstance().gui.leftHeight;
	}

	public static class Hotbar extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Hotbar {
		public Hotbar(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.Hotbar implements ZRenderGuiOverlay.Hotbar.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.Hotbar implements ZRenderGuiOverlay.Hotbar.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class Crosshair extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.Crosshair {
		public Crosshair(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.Crosshair implements ZRenderGuiOverlay.Crosshair.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.Crosshair implements ZRenderGuiOverlay.Crosshair.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class PlayerHealth extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.PlayerHealth {
		public PlayerHealth(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.PlayerHealth implements ZRenderGuiOverlay.PlayerHealth.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.PlayerHealth implements ZRenderGuiOverlay.PlayerHealth.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class ArmorLevel extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.ArmorLevel {
		public ArmorLevel(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.ArmorLevel implements ZRenderGuiOverlay.ArmorLevel.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.ArmorLevel implements ZRenderGuiOverlay.ArmorLevel.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class DebugText extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.DebugText {
		public DebugText(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.DebugText implements ZRenderGuiOverlay.DebugText.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.DebugText implements ZRenderGuiOverlay.DebugText.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class PotionIcons extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.PotionIcons {
		public PotionIcons(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.PotionIcons implements ZRenderGuiOverlay.PotionIcons.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.PotionIcons implements ZRenderGuiOverlay.PotionIcons.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}

	public static class ChatPanel extends ForgeZRenderGuiOverlay implements ZRenderGuiOverlay.ChatPanel {
		public ChatPanel(RenderGuiLayerEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderGuiOverlay.ChatPanel implements ZRenderGuiOverlay.ChatPanel.Pre {
			public Pre(RenderGuiLayerEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderGuiOverlay.ChatPanel implements ZRenderGuiOverlay.ChatPanel.Post {
			public Post(RenderGuiLayerEvent.Post e) {
				super(e);
			}
		}
	}
}
