package org.violetmoon.zetaimplforge.client.event.play;

import org.violetmoon.zeta.client.event.play.ZInput;

import net.minecraftforge.client.event.InputEvent;

public class ForgeZInput implements ZInput {
	public static class MouseButton extends ForgeZInput implements ZInput.MouseButton {
		private final InputEvent.MouseButton e;

		public MouseButton(InputEvent.MouseButton e) {
			this.e = e;
		}

		@Override
		public int getButton() {
			return e.getButton();
		}

		@Override
		public int getAction() {
			return e.getAction();
		}
	}

	public static class Key extends ForgeZInput implements ZInput.Key {
		private final InputEvent.Key e;

		public Key(InputEvent.Key e) {
			this.e = e;
		}

		@Override
		public int getKey() {
			return e.getKey();
		}

		@Override
		public int getAction() {
			return e.getAction();
		}

		@Override
		public int getScanCode() {
			return e.getScanCode();
		}
	}
}